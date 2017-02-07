###############################################################################################
###############################################################################################
###	    Utility Scriprt to be run after post deployment                                     ###
###     author : Sarath Nair (368142)       Date: 01-Aug-2013                               ###
###############################################################################################
###	                                                                                        ###
###          Execute this script directly after db deployment                               ###
###                                                                                         ###
###############################################################################################
###############################################################################################

#importing necessary modules
import sys
import json
import logging
import logging.config
import os
from bson import json_util
import datetime

import tj_db_connection  # tool for connecting to mongoDB


## Initiating logging
default_logging_path = 'logging.json'
default_level = logging.INFO
env_key = 'LOG_CFG'  # This environment variable can be set to load corresponding logging doc

#Reading loging configuration file
path = default_logging_path
value = os.getenv(env_key, None)
if value:
    path = value
if os.path.exists(path):
    with open(path, 'rt') as f:
        config = json.loads(f.read())
    logging.config.dictConfig(config)
else:
    logging.basicConfig(filename='tj.log',level=default_level)

#Reading the configuration file
logging.info('Reading tru junction config file')
try:
    current_working_directory = os.path.dirname(sys.argv[0])
    current_working_directory = os.path.abspath(current_working_directory)
    json_data = open(current_working_directory + '/master_data.json')
    data = json.load(json_data)
    data = json.dumps(data)
    data = json.loads(data, object_hook=json_util.object_hook)
    json_data.close()
except:
    logging.error('Cannot read from config file. Cause: %s',sys.exc_info()[1] )
    sys.exit(4)

try:
    tj_read_write_user_name = tj_db_connection.tj_read_write_user_name #Contains the DB read write user name
    tj_read_write_user_pwd = tj_db_connection.tj_read_write_user_pwd #Contains the DB read write user password
    tj_username= tj_db_connection.tj_username#Contains the tjuser username
    tj_userpwd = tj_db_connection.tj_userpwd#Contains the tjuser password
    master_data_setup_list = data['DATA_SETUP'] #Contains the DB and corresponding collection list for which master data has to be setup
    company_collection = tj_db_connection.company_collection #Contains the name of COMPANY collection
    tj_admin_user_name = tj_db_connection.tj_admin_user_name #Contains the login user name
    SSL = tj_db_connection.SSL
    mongoIP = tj_db_connection.mongo_ip
    mongoPort = tj_db_connection.mongo_port
except:
    logging.error('Error reading configuration values. Cause: %s',sys.exc_info()[1] ) #In case it can't read any of the config file it will throw error
    sys.exit(4)

logging.info('Successfully read from configuration file')

connection = tj_db_connection.create_connection()

db = connection['admin']
#authenticate tjuser
try:
    db.authenticate(tj_username,tj_userpwd) #Authenticating as read write user
except:
    logging.info('authenticate for mongodb3.0');
    try:
        db.authenticate(tj_username,tj_userpwd,mechanism='SCRAM-SHA-1')
    except:
        logging.error('Error authenticating to admin db. Cause: %s',sys.exc_info()[1]) #Authentication failure
        sys.exit(10)

db = connection['MASTER_DB']
'''
try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
except:
    logging.info('authenticate for mongodb3.0');
    try:
        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
    except:
        logging.error('Error authenticating to MASTER_DB. Cause: %s',sys.exc_info()[1]) #Authentication failure
        sys.exit(10)
'''
company_list = db['COMPANY'].aggregate([{'$project':{'COMPANY_ID':1,'_id':0}},{'$group':{'_id':'COMPANY_IDS','COMPANY':{'$addToSet':'$COMPANY_ID'}}}]).next()['COMPANY']

logging.info("Iterating company list")
for company_id in company_list:
    meta_db = str(int(company_id)) + '_META_DB'
    logging.info("Connecting to %s", meta_db)
    db = connection[meta_db]
    '''
    try:
        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
    except:
        logging.info('authenticate for mongodb3.0');
        try:
            db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
        except:
            logging.error('Error authenticating to %s. Cause: %s', meta_db,  sys.exc_info()[1]) #Authentication failure
            sys.exit(15)
    '''

    smsEntity = db['ENTITY_TEMPLATE'].find_one({"ENTITY_NAME": "SMS_NOTIFICATION", "IS_SYSTEM_GENERATED": True},{"ENTITY_ID":1})
    pushEntity = db['ENTITY_TEMPLATE'].find_one({"ENTITY_NAME": "PUSH_NOTIFICATION", "IS_SYSTEM_GENERATED": True},{"ENTITY_ID":1})
    emailEntity = db['ENTITY_TEMPLATE'].find_one({"ENTITY_NAME": "EMAIL_NOTIFICATION", "IS_SYSTEM_GENERATED": True},{"ENTITY_ID":1})

    smsService = db['SERVICE'].find_one({"SERVICE_NAME":"SMS Service", "IS_SYSTEM_GENERATED":True},{"SERVICE_ID":1})
    emailService = db['SERVICE'].find_one({"SERVICE_NAME":"Email Service", "IS_SYSTEM_GENERATED":True},{"SERVICE_ID":1})
    pushAndroidService = db['SERVICE'].find_one({"SERVICE_NAME":"Push Android Service", "IS_SYSTEM_GENERATED":True},{"SERVICE_ID":1})
    pushIOSService = db['SERVICE'].find_one({"SERVICE_NAME":"Push IOS", "IS_SYSTEM_GENERATED":True},{"SERVICE_ID":1})

    smsConnection = db['CONNECTION_SETTING'].find_one({"CONNECTION_NAME":"SMS Connection"},{"CONNECTION_ID":1})
    emailConnection = db['CONNECTION_SETTING'].find_one({"CONNECTION_NAME":"Email Connection"},{"CONNECTION_ID":1})
    pushAndroidConnection = db['CONNECTION_SETTING'].find_one({"CONNECTION_NAME":"Push Android"},{"CONNECTION_ID":1})
    pushIOSConnection = db['CONNECTION_SETTING'].find_one({"CONNECTION_NAME":"Push IOS"},{"CONNECTION_ID":1})
    updateObj = db['CONNECTION_SETTING'].update({ "CONNECTION_NAME" : "DB_Details"},{"$set":{"HOST_NAME":mongoIP,"PORT":mongoPort}})
    if updateObj['ok']:
        logging.info("Updated DB_Details connection")
    else:
        logging.error("Unable to DB_Details connection")
    orchData = db['ORCHESTRATION'].find();
    appData = db['APP'].find();
    #jobforPurge = db['SYSTEM_JOB'].find({"JOB_TYPE":"PURGE"},{"JOB_ID":1})
    currentDateTime = datetime.datetime.utcnow()
    CurrentDateTimeServer = datetime.datetime.now()
    datediff= currentDateTime-CurrentDateTimeServer
    scheduletime = currentDateTime.strftime("%Y-%m-%d 23:59:59.001")
    #print "%%%%%%%%%%%"
    time = datetime.datetime.strptime(scheduletime, "%Y-%m-%d %H:%M:%S.%f")
    #print time
    time = datediff+time
    schedule = {"WHEN_TO_START": "AT","FROM_DATE": currentDateTime,"TO_DATE": "","START_TIME": time,"FREQUENCY": "DAILY","NEXT_RUN_TIME": ""}
    jobstatus = [{"STATUS": "YET_TO_START","CREATED_DATE":currentDateTime}]
    Syncjobstatus = [{"STATUS": "STARTED","CREATED_DATE":currentDateTime}]
    updateObj = db['SYSTEM_JOB'].update({"JOB_CATEGORY": "TJ_DATA_SYNC","JOB_STATUS":{'$exists':False}},{"$set":{"JOB_STATUS":Syncjobstatus,"LAST_RUN_DATE":currentDateTime,"SCHEDULE":schedule,"CREATED_DATE":currentDateTime,"MODIFIED_DATE":currentDateTime,"IS_LOCKED":True,"LOCKED_DATE": ""}})
    #for eachJob in jobforPurge:
    updateObj = db['SYSTEM_JOB'].update({"LAST_RUN_DATE":{'$exists':False},"SCHEDULE":{'$exists':False},"JOB_STATUS":{'$exists':False},"JOB_CATEGORY": {"$ne":"TJ_DATA_SYNC"}},{"$set":{"LAST_RUN_DATE":currentDateTime,"SCHEDULE":schedule,"CREATED_DATE":currentDateTime,"MODIFIED_DATE":currentDateTime,"JOB_STATUS":jobstatus,"IS_LOCKED":False,"LOCKED_DATE": ""}},multi=True)
    if updateObj['ok']:
        logging.info("Updated DETAILS in SYSTEM_JOB")
	#jobstatus = [{"STATUS": "STARTED","CREATED_DATE":currentDateTime}]
	#updateObj = db['SYSTEM_JOB'].update({"JOB_CATEGORY": "TJ_DATA_SYNC","JOB_STATUS":{'$exists':False}},{"JOB_STATUS":jobstatus})
    #Updating the notification service with appropriate connection id
    logging.info("Updating notification services")
    updateObj = db['SERVICE'].update({"SERVICE_ID":smsService["SERVICE_ID"]},{"$set":{"CONNECTION_ID":smsConnection["CONNECTION_ID"]}})
    if updateObj['ok']:
        logging.info("Updated SMS Service")
    else:
        logging.error("Unable to update sms service")
    updateObj = db['SERVICE'].update({"SERVICE_ID":emailService["SERVICE_ID"]},{"$set":{"CONNECTION_ID":emailConnection["CONNECTION_ID"]}})
    if updateObj['ok']:
        logging.info("Updated Email Service")
    else:
        logging.error("Unable to update Email service")
    updateObj = db['SERVICE'].update({"SERVICE_ID":pushAndroidService["SERVICE_ID"]},{"$set":{"CONNECTION_ID":pushAndroidConnection["CONNECTION_ID"]}})
    if updateObj['ok']:
        logging.info("Updated Push Android Service")
    else:
        logging.error("Unable to Push Android service")
    updateObj = db['SERVICE'].update({"SERVICE_ID":pushIOSService["SERVICE_ID"]},{"$set":{"CONNECTION_ID":pushIOSConnection["CONNECTION_ID"]}})
    if updateObj['ok']:
        logging.info("Updated Push IOS Service")
    else:
        logging.error("Unable to update Push IOS service")
    logging.info("Finished Updating notification services")


    ##Updating HTTP_CODE and USE_HTTP_CODE for master data Services
    masterDataServiceList = ["LDAP_CONNECTION_Service",
                             "OAuth_Service",
                             "OAuthClientRegister",
                             "LocalDBUser",
                             "RegisterKeyEPS02",
                             "TruIDMapping_Service",
                             "Push Android Service",
                             "Email Service",
                             "Push IOS",
                             "SMS Service",
                             "DeviceRegistration",
                             "DeviceReMapping",
                             "DeviceMapFlagDetails",
                             "ValidateCsrf",
                             "ExpireCsrf",
                             "GetTransactionStatus",
							 "RegisterClientKey"
    ]
    for serviceName in masterDataServiceList:
        serviceData = db['SERVICE'].find_one({"SERVICE_NAME":serviceName, "IS_SYSTEM_GENERATED":True},{"SERVICE_ID":1,"HTTP_CODE":1,"USE_HTTP_CODE":1})
        logging.info("Updating %s service",serviceName)
        updateObj = db['SERVICE'].update({"SERVICE_ID":serviceData["SERVICE_ID"]},{"$set":{"HTTP_CODE":serviceData.get("HTTP_CODE",[]),"USE_HTTP_CODE":serviceData.get("USE_HTTP_CODE",False)}})
        if updateObj['ok']:
            logging.info("Updated %s Service", serviceName)
        else:
            logging.error("Unable to update %s service", serviceName)


    logging.info("Updating SMS eps")
    smsEPS = db['ORCHESTRATION'].find_one({"IDENTIFIER":"SMSEPS","IS_SYSTEM_GENERATED":True},{"ENTITY":1,"ORCHESTRATION_ID":1})
    for entity in smsEPS["ENTITY"]:
        entity['ENTITY_ID'] = smsEntity['ENTITY_ID']
        entity['ENTITY_ORIGIN_ID'] = smsEntity['ENTITY_ID']
        entity['SERVICE_EXECUTION_ORDER'][0]['SERVICE_ID'] = smsService['SERVICE_ID']
        for action in entity['ACTION']:
            for logicalDefinitions in action['LOGICAL_DEFINITIONS']:
                logicalDefinitions['SERVICE_ID'] = smsService['SERVICE_ID']
    updateObj = db['ORCHESTRATION'].update({"ORCHESTRATION_ID":smsEPS['ORCHESTRATION_ID']},{"$set":{"ENTITY":smsEPS['ENTITY']}})
    if updateObj['ok']:
        logging.info("Updated SMS eps")
    else:
        logging.error("Unable to update SMS eps")

    logging.info("Updating email eps")
    emailEPS = db['ORCHESTRATION'].find_one({"IDENTIFIER":"EmailEPS","IS_SYSTEM_GENERATED":True},{"ENTITY":1,"ORCHESTRATION_ID":1})
    for entity in emailEPS["ENTITY"]:
        entity['ENTITY_ID'] = emailEntity['ENTITY_ID']
        entity['ENTITY_ORIGIN_ID'] = emailEntity['ENTITY_ID']
        entity['SERVICE_EXECUTION_ORDER'][0]['SERVICE_ID'] = emailService['SERVICE_ID']
        for action in entity['ACTION']:
            for logicalDefinitions in action['LOGICAL_DEFINITIONS']:
                logicalDefinitions['SERVICE_ID'] = emailService['SERVICE_ID']
    updateObj = db['ORCHESTRATION'].update({"ORCHESTRATION_ID":emailEPS['ORCHESTRATION_ID']},{"$set":{"ENTITY":emailEPS['ENTITY']}})
    if updateObj['ok']:
        logging.info("Updated EMAIL eps")
    else:
        logging.error("Unable to update EMAIL eps")

    logging.info("Updating push eps")
    pushEPS = db['ORCHESTRATION'].find_one({"IDENTIFIER":"PushEPS","IS_SYSTEM_GENERATED":True},{"ENTITY":1,"ORCHESTRATION_ID":1})
    for entity in pushEPS["ENTITY"]:
        entity['ENTITY_ID'] = pushEntity['ENTITY_ID']
        entity['ENTITY_ORIGIN_ID'] = pushEntity['ENTITY_ID']
        entity['SERVICE_EXECUTION_ORDER'][0]['SERVICE_ID'] = pushAndroidService['SERVICE_ID']
        for action in entity['ACTION']:
            for logicalDefinitions in action['LOGICAL_DEFINITIONS']:
                if logicalDefinitions['SERVICE_ID'] == "ANDROID":
                    logicalDefinitions['SERVICE_ID'] = pushAndroidService['SERVICE_ID']
                elif logicalDefinitions['SERVICE_ID'] == "IOS":
                    logicalDefinitions['SERVICE_ID'] = pushIOSService['SERVICE_ID']
    updateObj = db['ORCHESTRATION'].update({"ORCHESTRATION_ID":pushEPS['ORCHESTRATION_ID']},{"$set":{"ENTITY":pushEPS['ENTITY']}})
    if updateObj['ok']:
        logging.info("Updated push eps")
    else:
        logging.error("Unable to update push eps")

    globalBucket = db['BUCKET'].find_one({"IDENTIFIER": "GLOBAL_BUCKET", "IS_SYSTEM_GENERATED": True}, {"BUCKET_ID": 1})
    try:
        db.create_collection('PROCESS_QUEUE_OFFLINE_INDEX',size=100000000,capped=True)
    except:
        logging.error('Collection already exists')
    security_db = str(int(company_id)) + '_SECURITY_DB'
    logging.info("Connecting to %s", security_db)
    db = connection[security_db]
    '''
    try:
        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
    except:
        logging.info('authenticate for mongodb3.0');
        try:
            db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
        except:
            logging.error('Error authenticating to %s. Cause: %s', security_db,  sys.exc_info()[1]) #Authentication failure
            sys.exit(15)
    '''
    globalBucketResource = {"RESOURCE_ID": globalBucket["BUCKET_ID"], "RESOURCE_IDENTIFIER": "GLOBAL_BUCKET",
                            "RESOURCE_TYPE": "BUCKET", "PRIVILEGE": ["R","W","D"], "ENTITY": []}
    col = db["ROLE"].find({"ROLE_NAME": "tjadminrole", "IS_CUSTOM_ROLE": True,
                           "RESOURCE_PRIVILEGE.RESOURCE_IDENTIFIER": "GLOBAL_BUCKET"}, limit=1)
    roleData = db["ROLE"].find_one({"ROLE_NAME": "tjadminrole"});
    roleId = roleData['ROLE_ID']
    db['USER'].update({"USER_ID":"tjadmin"},{"$addToSet":{"ROLE_ID":roleId}})
	
    roleDataAppadmin = db["ROLE"].find_one({"ROLE_NAME": "APP_ADMIN"});
    #roleIdAppadmin = roleDataAppadmin['ROLE_ID']
    #db['USER'].update({"USER_ID":"appadmin"},{"$addToSet":{"ROLE_ID":roleIdAppadmin}})
	
    db["ROLE"].update({"ROLE_NAME": "tjadminrole", "IS_CUSTOM_ROLE": True},
                      {"$addToSet": {"USER_ID": tj_admin_user_name}})
    db["ROLE"].update({"ROLE_NAME": "AllAuthenticatedUsers", "IS_CUSTOM_ROLE": True},
                      {"$addToSet": {"USER_ID": tj_admin_user_name}})
					  
    db["ROLE"].update({"ROLE_NAME": "APP_ADMIN", "IS_CUSTOM_ROLE": True},
                      {"$addToSet": {"USER_ID": "appadmin"}})
    if col.count() == 0:
        updateObj = db["ROLE"].update({"ROLE_NAME": "tjadminrole", "IS_CUSTOM_ROLE": True},
                          {"$push": {"RESOURCE_PRIVILEGE": globalBucketResource}})
        if updateObj['ok']:
            logging.info("Updated tjadminrole with default resource privilege")
        else:
            logging.error("Unable to update tjadminrole with default resource privilege")
    col = db["ROLE"].find({"ROLE_NAME": "AllAuthenticatedUsers", "IS_CUSTOM_ROLE": True,
                           "RESOURCE_PRIVILEGE.RESOURCE_IDENTIFIER": "GLOBAL_BUCKET"}, limit=1)
    if col.count() == 0:
        db["ROLE"].update({"ROLE_NAME": "AllAuthenticatedUsers", "IS_CUSTOM_ROLE": True},
                          {"$push": {"RESOURCE_PRIVILEGE": globalBucketResource}})
        if updateObj['ok']:
            logging.info("Updated AllAuthenticatedUsers with default resource privilege")
        else:
            logging.error("Unable to update AllAuthenticatedUsers with default resource privilege")
