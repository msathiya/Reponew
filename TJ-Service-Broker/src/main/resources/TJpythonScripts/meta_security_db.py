import sys
import logging
import logging.config
import os
import pexpect

from json import loads
from pymongo import MongoClient

import tj_db_connection

## Initiating logging
default_logging_path = 'logging.json'
default_level = logging.INFO
env_key = 'LOG_CFG'  # This environment variable can be set to load corresponding logging doc

#Reading logging configuration file
path = default_logging_path
value = os.getenv(env_key, None)
if value:
    path = value
if os.path.exists(path):
    with open(path, 'rt') as f:
        config = loads(f.read())
    logging.config.dictConfig(config)
else:
    logging.basicConfig(filename='tj.log',level=default_level)

current_working_directory = os.path.dirname(sys.argv[0])
current_working_directory= os.path.abspath(current_working_directory)

try:
    admin_name = tj_db_connection.admin_name
    admin_pwd = tj_db_connection.admin_pwd
    tj_read_write_user_name = tj_db_connection.tj_read_write_user_name
    tj_read_write_user_pwd = tj_db_connection.tj_read_write_user_pwd
    tj_read_write_user_roles = tj_db_connection.tj_read_write_user_roles
    tj_read_user_name = tj_db_connection.tj_read_user_name
    tj_read_user_pwd = tj_db_connection.tj_read_user_pwd
    tj_read_user_roles = tj_db_connection.tj_read_user_roles
    tj_username= tj_db_connection.tj_username#Contains the tjuser username
    tj_userpwd = tj_db_connection.tj_userpwd#Contains the tjuser password
    company_collection = tj_db_connection.company_collection
    #is_meta_db_sharded = data['META_DB']['IS_SHARDED']
    SSL = tj_db_connection.SSL
except:
    logging.error('Error reading configuration values. Cause: %s',sys.exc_info()[1] )
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
# get the arguments
company_Id = str(sys.argv[1])
meta_db = company_Id + "_" + "META_DB"
security_db = company_Id + "_" + "SECURITY_DB"
rule_db = company_Id + "_" + "RULE_DB"
context_db = company_Id + "_" + "CONTEXT_DB"
notification_db = company_Id + "_" + "NOTIFICATION_DB"
app_storage_db = company_Id + "_" + "APP_STORAGE_DB"
app_log_db = company_Id + "_" + "APP_LOG_DB"

# checking weather company data present in Master DB
logging.info('Authenticating MASTER_DB')
#authenticating to master db
db = connection['MASTER_DB']
'''
try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd)
except:
    logging.info('authenticate for mongodb3.0');
    try:
        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
    except:
        logging.error('Error authenticating to MASTER_DB. Cause: %s',sys.exc_info()[1])
        sys.exit(10)

logging.info('Successfully authenticated to MASTER_DB')
'''
#retrieve company details from COMPANY collection
logging.info('Retrieving company details for company_id %s',company_Id)
findObject = {}

try:
    findObject[company_collection[1]] = int(company_Id)
except:
    logging.error('Input error for Company_Id - %s',company_Id)
    sys.exit(12)

cursor = db[company_collection[0]].find(findObject,{'_id':1}).limit(1)
if cursor.count() == 0:
    logging.error('Company details not found in MASTER_DB.%s for Company_Id %s',company_collection[0],company_Id)
    sys.exit(9)

logging.info('Company details found in MASTER_DB')
'''
logging.info('Authenticating admin db')
#authenticating to admin db
db = connection['admin']
try:
    db.authenticate(admin_name, admin_pwd)
except:
    logging.info('authenticate for mongodb3.0');
    try:
        db.authenticate(admin_name, admin_pwd,mechanism='SCRAM-SHA-1')

    except:
        logging.error('Error authenticating to monngodb. Cause: %s',sys.exc_info()[1] )
        sys.exit(3)

logging.info('Successfully authenticated as admin')
'''
#create metaDB
logging.info('Connecting to %s', meta_db)
db = connection[meta_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', meta_db)
#adding users to meta_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd, roles=tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd, roles=tj_read_user_roles)
logging.info('Created users for Meta db %s', meta_db)

logging.info('Authenticating db %s', meta_db)

try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd)
except:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
'''
#adding json_schema
logging.info('Creating JSON schemas and indexes for static collections in %s', meta_db)
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_json_schema.py" + ' ' + meta_db)
    #os.system(To_execute)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_json_schema.py" + ' ' + meta_db
    os.system(To_execute)
#    try:
#         shard_keys = loads(data[meta_db_collection]['SHARD_KEY'])
#         logging.info('Going to shard collection %s', collection_name)
#         try:
#             connection.admin.command('shardCollection', meta_db + '.' + collection_name, key = shard_keys)
#         except:
#             logging.error('Error sharding %s, Cause: %s',collection_name, sys.exc_info()[1])
#         logging.info('Sharded collection %s', collection_name)			 
#    except:
#         continue
logging.info('Completed creation of JSON schemas and indexes for static schemas in %s', meta_db)

#adding master data
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_master_data.py" + ' ' + meta_db)
    #os.system(To_execute)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_master_data.py" + ' ' + meta_db
    os.system(To_execute)


########################################################################################
#########################    SECURITY DB SECTION   #####################################
########################################################################################

logging.info('Connecting to %s', security_db)

#connect to security_db
db = connection[security_db]

#if (is_security_db_sharded == 'Y'):
#    logging.info('Sharding security db - %s', security_db)
#    try:
#        connection.admin.command('enablesharding',security_db) #enables sharding on security_db		
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',security_db,sys.exc_info()[1])
#        sys.exit(2)	
#    logging.info('Created and sharded %s', security_db)

'''
logging.info('Adding users to %s', security_db)
#adding users to security_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for security db %s', security_db)

logging.info('Authenticating db %s', security_db)
try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd)
except:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
'''
#adding json_schema
logging.info('Creating JSON schemas and indexes for static collections in %s', security_db)
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_json_schema.py" + ' ' + security_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_json_schema.py" + ' ' + security_db
    os.system(To_execute)
logging.info('Completed creation of JSON schemas and indexes for static schemas in %s', security_db)

#adding master data
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_master_data.py" + ' ' + security_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_master_data.py" + ' ' + security_db
    os.system(To_execute)


########################################################################################
#########################      RULE DB SECTION     #####################################
########################################################################################

logging.info('Connecting to %s', rule_db)
db = connection[rule_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', rule_db)
#adding users to rule_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for rule db %s', rule_db)

logging.info('Authenticating db %s', rule_db)
try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd)
except:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
'''
#adding json_schema
logging.info('Creating JSON schemas and indexes for static collections in %s', rule_db)

if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_json_schema.py" + ' ' + rule_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_json_schema.py" + ' ' + rule_db
    os.system(To_execute)
logging.info('Completed creation of JSON schemas and indexes for static schemas in %s', rule_db)

#adding master data
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_master_data.py" + ' ' + rule_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_master_data.py" + ' ' + rule_db
    os.system(To_execute)

########################################################################################
#########################      CONTEXT DB SECTION     #####################################
########################################################################################

logging.info('Connecting to %s', context_db)
db = connection[context_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', context_db)
#adding users to rule_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for rule db %s', context_db)
'''
#adding master data
if SSL == 'Y':
    sslPEMKeyPassword = tj_db_connection.sslPEMKeyPassword
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_master_data.py" + ' ' + context_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_master_data.py" + ' ' + context_db
    os.system(To_execute)

########################################################################################
#########################      NOTIFICATION DB SECTION     #####################################
########################################################################################

logging.info('Connecting to %s', notification_db)
db = connection[notification_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', notification_db)
#adding users to rule_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for notification db %s', notification_db)

logging.info('Authenticating db %s', notification_db)
try:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd)
except:
    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
'''
#adding json_schema
logging.info('Creating JSON schemas and indexes for static collections in %s', notification_db)

if SSL == 'Y':
    sslPEMKeyPassword = data['COMMON_SETTINGS']['sslPEMKeyPassword']
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_json_schema.py" + ' ' + notification_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_json_schema.py" + ' ' + notification_db
    os.system(To_execute)
logging.info('Completed creation of JSON schemas and indexes for static schemas in %s', notification_db)



########################################################################################
#########################      APP_STORAGE DB SECTION     #####################################
########################################################################################

logging.info('Connecting to %s', app_storage_db)
db = connection[app_storage_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', app_storage_db)
#adding users to rule_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for app storage db %s', app_storage_db)
'''
#adding json_schema
logging.info('Creating JSON schemas and indexes for static collections in %s', app_storage_db)

if SSL == 'Y':
    sslPEMKeyPassword = data['COMMON_SETTINGS']['sslPEMKeyPassword']
    To_execute = pexpect.spawn("python " + current_working_directory + "/insert_json_schema.py" + ' ' + app_storage_db)
    To_execute.expect("Enter PEM pass phrase:", timeout=5)
    To_execute.sendline(sslPEMKeyPassword)
    To_execute.sendline(sslPEMKeyPassword)
else:
    To_execute = "python " + current_working_directory + "/insert_json_schema.py" + ' ' + app_storage_db
    os.system(To_execute)
logging.info('Completed creation of JSON schemas and indexes for static schemas in %s', app_storage_db)


########################################################################################
#########################      APP_LOG DB SECTION     #####################################
########################################################################################

logging.info('Connecting to %s', app_log_db)
db = connection[app_log_db]

#if (is_meta_db_sharded == 'Y'):
#    logging.info('Sharding meta db - %s', meta_db)
#    try:
#        connection.admin.command('enablesharding',meta_db) #enables sharding on meta_db
#    except:
#        logging.error('Cannot shard the database %s. Cause: %s',meta_db,sys.exc_info()[1])
#        sys.exit(2)
#    logging.info('Created and sharded %s', meta_db)
'''
logging.info('Adding users to %s', app_log_db)
#adding users to rule_db
db.add_user(tj_read_write_user_name,tj_read_write_user_pwd,roles = tj_read_write_user_roles)
db.add_user(tj_read_user_name,tj_read_user_pwd,roles = tj_read_user_roles)
logging.info('Created users for app log db %s', app_log_db)
'''