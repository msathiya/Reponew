###############################################################################################
###############################################################################################
###	    Scriprt for inserting/updating master data                                          ###
###     author : Sarath Nair (368142)       Date: 03-Jan-2013                               ###
###############################################################################################
###	         Execute this script by passing the following two optional parameters           ###
###          1. DB Name        		                                                        ###
###          2. Collection Name                                                             ###
###############################################################################################
###############################################################################################

#importing necessary modules
import sys
import json
import logging
import logging.config
import os
from bson import json_util

import tj_db_connection  # tool for connecting to mongoDB

print 'In Inert master data file'
def insert_collection_sequence(db_name):
    db = connection[db_name]
    '''
    try:
        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
    except:
        logging.info('authenticate for mongodb3.0');
        try:
            db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
        except:
            print 'Error in Authenticating Mongo DB'
            logging.error('Error authenticating to %s. Cause: %s',db_name,  sys.exc_info()[1]) #Authentication failure
            sys.exit(15)
    '''
    db_root = db_name.lstrip('0123456789_')
    data_list = data["SEQUENCE_GENERATOR"][db_root]
    for key in data_list:
        data_list[key]["COLLECTION_NAME"] = key
        result_next_insert = db[key].find().sort(data_list[key]["ATTRIBUTE_SEQUENCE"][0]["ATTRIBUTE_NAME"],-1).limit(1)
        logging.info("Inserting collection sequence data for collection: " + key)
        if result_next_insert.count() == 0:
            db["SEQUENCE_GENERATOR"].remove({"COLLECTION_NAME": key})
            db["SEQUENCE_GENERATOR"].insert(data_list[key])
        else:
            db["SEQUENCE_GENERATOR"].remove({"COLLECTION_NAME": key})
            data_list[key]["ATTRIBUTE_SEQUENCE"][0]["SEQUENCE_CURRENT_VALUE"] = result_next_insert[0][data_list[key]["ATTRIBUTE_SEQUENCE"][0]["ATTRIBUTE_NAME"]]
            db["SEQUENCE_GENERATOR"].insert(data_list[key])



#####################################################################
####                  Function Definition                         ####
####   Function Name : insert_master_data                        ####
####   Argument1 : db_name                                       ####
####   Argument2 : collection_name (Optional)                    ####
####   Argument3 : is_db_specific (Optional)                     ####
####   This function is responsible for inserting master data    ####
#####################################################################
def insert_master_data(db_name, collection_name="", is_db_specific=False):
    #Checking if master data is to be inserted to MASTER_DB
    if db_name == 'MASTER_DB':
        if collection_name == "":
            collection_list = master_data_setup_list[db_name] #Get the collection names for which master data has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        db = connection['admin']
        '''
        try:
            db.add_user(admin_name,admin_pwd, roles=admin_role)
        except:
            logging.info('user already exists admin db')
        try:
            db.add_user(tj_username,tj_userpwd, roles=tj_userrole)
        except:
            logging.info('tjuser already exists admin db')
        '''
        #authenticate tjuser
        try:
            db.authenticate(admin_name,admin_pwd); #Authenticating as read write user
        except:
            logging.info('authenticate for mongodb3.0');
            try:
                db.authenticate(tj_username,tj_userpwd,mechanism='SCRAM-SHA-1')
            except:
                logging.error('Error authenticating to admin db. Cause: %s',sys.exc_info()[1]) #Authentication failure
                sys.exit(10)
        #Iterating collection_list
        db = connection[db_name]
        insert_collection_sequence(db_name)
       # if 'COMPANY' in collection_list:
       #     collection_list.remove('COMPANY')
        for collection in collection_list:
            logging.info('Inserting master data for %s in %s', collection , db_name )
            try:
                primary_attributes = data[db_name]["COLLECTIONS"][collection]['PRIMARY_KEY']
                unique_id_attribute = data[db_name]["COLLECTIONS"][collection]['UNIQUE_ID_FIELD']
            except:
                logging.error('No PRIMARY_KEY / UNIQUE_ID_FIELD found for %s in %s. Continuing with master data creation', collection, db_name)
                continue
            try:
                master_datas = data[db_name]["COLLECTIONS"][collection]['MASTER_DATA'] #Get the master data (json) for the collection
            except:
                logging.error('No master data found for %s in %s. Continuing with master data creation', collection , db_name )
                continue
            #Connecting to MASTER_DB
            db = connection[db_name]
            '''
            try:
                db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
            except:
                logging.info('authenticate for mongodb3.0');
                try:
                    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
                except:
                    logging.error('Error authenticating to %s. Cause: %s',db_name,  sys.exc_info()[1]) #Authentication failure
                    sys.exit(15)
            '''
            #Inserting master data
            query_object = {}
            for master_data in master_datas:  #Iterating through each master data
                for primary_attribute in primary_attributes:
                    query_object[primary_attribute] = master_data.get(primary_attribute) #building the query object using the primary definitions
                result_query = db[collection].find(query_object).limit(1)
                if result_query.count() != 0:
                    logging.info('Updating master data for %s',  query_object)
                    if unique_id_attribute != "":
                        master_data[unique_id_attribute] = result_query[0][unique_id_attribute]
                    db[collection].update(query_object, {"$set": master_data})
                else:
                    #result_next_insert = db[collection].find().sort("_id",-1).limit(1)
                    if unique_id_attribute != "":
                        seq_query_obj = {}
                        seq_query_obj["COLLECTION_NAME"] = collection
                        seq_query_obj["ATTRIBUTE_SEQUENCE.ATTRIBUTE_NAME"] = unique_id_attribute
                        update_obj = {"$inc": {"ATTRIBUTE_SEQUENCE.$.SEQUENCE_CURRENT_VALUE": 1}}
                        result_next_insert = db["SEQUENCE_GENERATOR"].find_and_modify(query=seq_query_obj,
                                                                                       update=update_obj, new=True)
                        master_data[unique_id_attribute] = result_next_insert["ATTRIBUTE_SEQUENCE"][0]["SEQUENCE_CURRENT_VALUE"]
                        logging.info('Inserting master data for %s',  query_object)
                        db[collection].insert(master_data)
                    else:
                        logging.info('Inserting master data for %s',  query_object)
                        db[collection].insert(master_data)
    #Checking whether DB is META /SECURITY / RULE / EVNET_LOG_NON_TJ DB
    elif db_name in ['META_DB', 'SECURITY_DB', 'RULE_DB', 'CONTEXT_DB', 'EVENT_LOG_DB', 'NOTIFICATION_DB']:
        db = connection['admin']
        try:
            db.authenticate(tj_username,tj_userpwd) #Authenticating as read write user
        except:
            logging.info('authenticate for mongodb3.0');
            try:
                db.authenticate(tj_username,tj_userpwd,mechanism='SCRAM-SHA-1')
            except:
                logging.error('Error authenticating to admin db. Cause: %s',sys.exc_info()[1]) #Authentication failure
                sys.exit(10)
        #Connecting to MASTER_DB
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
        company_list = 	db['COMPANY'].aggregate([{'$project':{'COMPANY_ID':1,'_id':0}},{'$group':{'_id':'COMPANY_IDS','COMPANY':{'$addToSet':'$COMPANY_ID'}}}]).next()['COMPANY'] #Getting list of companies (get the COMPANY IDs) from MASTER_DB
        if collection_name == "":
            collection_list = master_data_setup_list[db_name] #Get the collection names for which master data has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        #Iterating each company
        for company_id in company_list:
            id_db_name = str(int(company_id)) + '_' + db_name
            insert_collection_sequence(id_db_name)
            #Iterating collections from collection_list
            for collection in collection_list:
                logging.info('Inserting master data for %s in %s', collection, id_db_name )
                try:
                    primary_attributes = data[db_name]["COLLECTIONS"][collection]['PRIMARY_KEY']
                    unique_id_attribute = data[db_name]["COLLECTIONS"][collection]['UNIQUE_ID_FIELD']
                except:
                    logging.error('No PRIMARY_KEY / UNIQUE_ID_FIELD found for %s in %s. Continuing with master data creation', collection, db_name)
                    continue
                try:
                    master_datas = data[db_name]["COLLECTIONS"][collection]['MASTER_DATA'] #Get the master data (json) for the collection
                except:
                    logging.error('No master data found for %s in %s. Continuing with master data creation', collection , db_name )
                    continue
                db = connection[id_db_name] #Connecting to database
                '''
                try:
                    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
                except:
                    logging.info('authenticate for mongodb3.0');
                    try:
                        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
                    except:
                        logging.error('Error authenticating to %s. Cause: %s',id_db_name,  sys.exc_info()[1]) #Authentication failure
                        sys.exit(15)
                '''
                #Removing collection and inserting master data                
                query_object = {}
                for master_data in master_datas:  #Iterating through each master data
                    for primary_attribute in primary_attributes:
                        query_object[primary_attribute] = master_data.get(primary_attribute) #building the query object using the primary definitions
                    result_query = db[collection].find(query_object).limit(1)
                    if result_query.count() != 0:
                        logging.info('Updating master data for %s',  query_object)
                        if unique_id_attribute != "":
                            master_data[unique_id_attribute] = result_query[0][unique_id_attribute]
                        db[collection].update(query_object, {"$set": master_data})
                    else:
                        #result_next_insert = db[collection].find().sort("_id",-1).limit(1)
                        if unique_id_attribute != "":
                            seq_query_obj = {}
                            seq_query_obj["COLLECTION_NAME"] = collection
                            seq_query_obj["ATTRIBUTE_SEQUENCE.ATTRIBUTE_NAME"] = unique_id_attribute
                            update_obj = {"$inc": {"ATTRIBUTE_SEQUENCE.$.SEQUENCE_CURRENT_VALUE": 1}}
                            result_next_insert = db["SEQUENCE_GENERATOR"].find_and_modify(query=seq_query_obj,update=update_obj, new=True)

                            master_data[unique_id_attribute] = result_next_insert["ATTRIBUTE_SEQUENCE"][0]["SEQUENCE_CURRENT_VALUE"]
                            logging.info('Inserting master data for %s',  query_object)
                            db[collection].insert(master_data)
                        else:
                            logging.info('Inserting master data for %s',  query_object)
                            db[collection].insert(master_data)
    elif is_db_specific == True:
        db = connection['admin']
        try:
            db.authenticate(tj_username,tj_userpwd) #Authenticating as read write user
        except:
            logging.info('authenticate for mongodb3.0');
            try:
                db.authenticate(tj_username,tj_userpwd,mechanism='SCRAM-SHA-1')
            except:
                logging.error('Error authenticating to admin db. Cause: %s',sys.exc_info()[1]) #Authentication failure
                sys.exit(10)
        db_root = db_name.lstrip('0123456789_') #Removing leading numerals and _ character to get the root DB name
        if collection_name == "":
            collection_list = master_data_setup_list[db_root] #Get the collection names for which master data has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specifies collection_list will hold the name of just one collection
        insert_collection_sequence(db_name)
        for collection in collection_list:
            logging.info('Inserting master data for %s in %s', collection , db_name )
            try:
                primary_attributes = data[db_root]["COLLECTIONS"][collection]['PRIMARY_KEY']
                unique_id_attribute = data[db_root]["COLLECTIONS"][collection]['UNIQUE_ID_FIELD']
            except:
                logging.error('No PRIMARY_KEY / UNIQUE_ID_FIELD found for %s in %s. Continuing with master data creation', collection, db_name)
                continue
            try:
                master_datas = data[db_root]["COLLECTIONS"][collection]['MASTER_DATA'] #Get the master data (json) for the collection
            except:
                logging.error('No master data found for %s in %s. Continuing with master data creation', collection , db_name )
                continue
            db = connection[db_name] #Conencting to database
            '''
            try:
                db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
            except:
                logging.info('authenticate for mongodb3.0');
                try:
                    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
                except:
                    logging.error('Error authenticating to %s. Cause: %s',db_name,  sys.exc_info()[1]) #Authentication failure
                    sys.exit(15)
            '''
            #Removing collection and inserting master data
            query_object = {}
            for master_data in master_datas:  #Iterating through each master data
                for primary_attribute in primary_attributes:
                    query_object[primary_attribute] = master_data.get(primary_attribute) #building the query object using the primary definitions
                result_query = db[collection].find(query_object).limit(1)
                if result_query.count() != 0:
                    logging.info('Updating master data for %s',  query_object)
                    if unique_id_attribute != "":
                        master_data[unique_id_attribute] = result_query[0][unique_id_attribute]
                    db[collection].update(query_object, {"$set": master_data})
                else:
                    #result_next_insert = db[collection].find().sort("_id",-1).limit(1)
                    if unique_id_attribute != "":
                        seq_query_obj = {}
                        seq_query_obj["COLLECTION_NAME"] = collection
                        seq_query_obj["ATTRIBUTE_SEQUENCE.ATTRIBUTE_NAME"] = unique_id_attribute
                        update_obj = {"$inc": {"ATTRIBUTE_SEQUENCE.$.SEQUENCE_CURRENT_VALUE": 1}}
                        result_next_insert = db["SEQUENCE_GENERATOR"].find_and_modify(query=seq_query_obj,
                                                                                       update=update_obj, new=True)
                        master_data[unique_id_attribute] = result_next_insert["ATTRIBUTE_SEQUENCE"][0]["SEQUENCE_CURRENT_VALUE"]
                        logging.info('Inserting master data for %s',  query_object)
                        db[collection].insert(master_data)
                    else:
                        logging.info('Inserting master data for %s',  query_object)
                        db[collection].insert(master_data)

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
    tj_read_write_user_roles = tj_db_connection.tj_read_write_user_roles
    admin_name = tj_db_connection.admin_name#Contains the DB admin user name
    admin_pwd = tj_db_connection.admin_pwd#Contains the DB admin user pwd
    admin_role = tj_db_connection.admin_role#Contains the DB admin user role
    tj_username= tj_db_connection.tj_username#Contains the tjuser username
    tj_userpwd = tj_db_connection.tj_userpwd#Contains the tjuser password
    tj_userrole = tj_db_connection.tj_userrole#Contains the tjuser role
    master_data_setup_list = data['DATA_SETUP'] #Contains the DB and corresponding collection list for which master data has to be setup
    company_collection = tj_db_connection.company_collection #Contains the name of COMPANY collection
    SSL = tj_db_connection.SSL
except:
    logging.error('Error reading configuration values. Cause: %s',sys.exc_info()[1] ) #In case it can't read any of the config file it will throw error
    sys.exit(4)

logging.info('Successfully read from configuration file')	

connection = tj_db_connection.create_connection()

# get the arguments
argument_Length = len(sys.argv)

if argument_Length < 2:
    # for key in data["DATA_SETUP"].keys():
    #     logging.info('Inserting master data for ' + key)
    #     insert_master_data(key)
    #     logging.info('Inserted master data for ' + key)
    logging.info('Inserting master data for MASTER_DB')
    insert_master_data('MASTER_DB') #Inserting master data for MASTER_DB
    logging.info('Inserted master data for MASTER_DB')
    logging.info('Inserting master data for META_DB')
    insert_master_data('META_DB') #Inserting master data for META_DB
    logging.info('Inserted master data for META_DB')
    logging.info('Inserting master data for SECURITY_DB')
    insert_master_data('SECURITY_DB') #Inserting master data for SECURITY_DB
    logging.info('Inserted master data for SECURITY_DB')
    logging.info('Inserting master data for RULE_DB')
    insert_master_data('RULE_DB') #Inserting master data for RULE_DB
    logging.info('Inserted master data for RULE_DB')
    logging.info('Inserting master data for CONTEXT_DB')
    insert_master_data('CONTEXT_DB') #Inserting master data for CONTEXT_DB
    logging.info('Inserted master data for CONTEXT_DB')
    logging.info('Inserting master data for NOTIFICATION_DB')
    insert_master_data('NOTIFICATION_DB') #Inserting master data for NOTIFICATION_DB
    logging.info('Inserted master data for NOTIFICATION_DB')
elif argument_Length == 2: #db name is given
    database_name = str(sys.argv[1]) #get the DB name from system argument
    if database_name[0].isdigit(): #Check if db_name is specific or general
        insert_master_data(database_name,"", True) #Inserting master data for specific database
        print 'Updated master data for ' + database_name
    else:
        insert_master_data(database_name) #Inserting master data for database(s)
        print 'Updated master data for ' + database_name
elif argument_Length == 3: #db and collection name is given
    database_name = str(sys.argv[1]) #get the DB name from system argument
    collection_name = str(sys.argv[2]) #get the collection name from system argument
    if database_name[0].isdigit():
        insert_master_data(database_name,collection_name, True) #Inserting master data for specific collection in specific database
        print 'Updated master data for ' + collection_name + ' in ' + database_name
    else:
        insert_master_data(database_name, collection_name) #Inserting master data for specific collection in database(s)
        print 'Updated master data for ' + collection_name + ' in ' + database_name
