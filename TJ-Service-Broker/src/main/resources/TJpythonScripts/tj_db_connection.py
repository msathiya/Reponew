__author__ = '368142'

from pymongo import MongoClient
import logging
import logging.config
import json
import os
import sys

logging.info('Reading tru junction config file')

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
        config = json.loads(f.read())
    logging.config.dictConfig(config)
else:
    logging.basicConfig(filename='tj.log', level=default_level)

current_working_directory = os.path.dirname(sys.argv[0])
current_working_directory = os.path.abspath(current_working_directory)

logging.info('Reading tru junction config file')
try:
    json_data = open(current_working_directory + '/config.json')
    data = json.load(json_data)
    json_data.close()
    commom_settings_temp = data["COMMON_SETTINGS"]
    json_data = open(current_working_directory + '/app.json')
    app_data = json.load(json_data)
    data.update(app_data)
    json_data.close()
    commom_settings_temp.update(data["COMMON_SETTINGS"])
    data["COMMON_SETTINGS"] = commom_settings_temp
    is_config_new = True
except:
    logging.warning('Cannot read config.json or app.json, Please make sure these files are present')
    logging.warning('Trying to read old configuration file. This is not recommended')
    try:
        json_data = open(current_working_directory + '/configurationfile.json')
        data = json.load(json_data)
        json_data.close()
        is_config_new = False
    except:
        logging.error('Cannot read from config file. Cause: %s', sys.exc_info()[1])
        sys.exit(4)
logging.info("Successfully read from config file")


if is_config_new:
    try:
        admin_name = data['COMMON_SETTINGS']['admin']['username']
        admin_pwd = data['COMMON_SETTINGS']['admin']['password']
        tj_read_write_user_name = data['COMMON_SETTINGS']['readwrite']['username']
        tj_read_write_user_pwd = data['COMMON_SETTINGS']['readwrite']['password']
        tj_read_write_user_roles = data['COMMON_SETTINGS']['readwrite']['roles']
        tj_read_user_name = data['COMMON_SETTINGS']['read']['username']
        tj_read_user_pwd = data['COMMON_SETTINGS']['read']['password']
        tj_read_user_roles = data['COMMON_SETTINGS']['read']['roles']
        tj_admin_user_name = data['COMMON_SETTINGS']['Trujunction_username']
        mongodump_path = data['COMMON_SETTINGS']['Mongo_Dump_Path']
        if "mongo" in data:
            mongo_ip = data["mongo"][0]["mongo1"]["IP_Address"]
            mongo_port = data["mongo"][0]["mongo1"]["port"]
        elif "mongoS" in data:
            mongo_ip = data["mongoS"][0]["mongoS1"]["IP_Address"]
            mongo_port = data["mongoS"][0]["mongoS1"]["port"]
    except:
        logging.error('Error reading configuration values. Cause: %s', sys.exc_info()[1])
        sys.exit(4)
else:
    try:
        admin_name = data['USER_ADMIN']['USERNAME']
        admin_pwd = data['USER_ADMIN']['PASSWORD']
        admin_role = data['USER_ADMIN']['roles'].split()
        tj_username = data['TJ_USER']['USERNAME']
        tj_userpwd = data['TJ_USER']['PASSWORD']
        tj_userrole = data['TJ_USER']['roles'].split()
        tj_read_write_user_name = data['USER_READ_WRITE']['USERNAME']
        tj_read_write_user_pwd = data['USER_READ_WRITE']['PASSWORD']
        tj_read_write_user_roles = data['USER_READ_WRITE']['roles'].split()
        tj_read_user_name = data['USER_READ']['USERNAME']
        tj_read_user_pwd = data['USER_READ']['PASSWORD']
        tj_read_user_roles = data['USER_READ']['roles'].split()
        mongo_ip = data["MONGOS_CONFIG"]["IP"]
        mongo_port = data["MONGOS_CONFIG"]["PORT"]
        mongodump_path = data['COMMON_SETTINGS']['mongodump_path']
        tj_admin_user_name = data['COMMON_SETTINGS']['Trujunction_username']
    except:
        logging.error('Error reading configuration values. Cause: %s', sys.exc_info()[1])
        sys.exit(4)

try:
    SSL = data['COMMON_SETTINGS']['enable_ssl']
    sslPEMKeyPassword = data['COMMON_SETTINGS']['sslPEMKeyPassword']
    isSharded = data['COMMON_SETTINGS']['isSharded']
except:
    SSL = "N"
    sslPEMKeyPassword = ""
    isSharded = "false"

company_collection = ['COMPANY', 'COMPANY_ID']
app_collection = ['APP', 'APP_ID']
json_schema_collection_name = "COLLECTION_JSON_SCHEMA"


def create_connection():
    connection = None
    if "mongo" in data:
        mongo_ip = data["mongo"][0]["mongo1"]["IP_Address"]
        mongo_port = data["mongo"][0]["mongo1"]["port"]
        logging.info("Trying to connect to mongo instance %s:%s", mongo_ip, mongo_port)
        try:
            if SSL == 'Y':
                ssl_ca_file = data['COMMON_SETTINGS']['sslCAFile']
                sslpemkeyfile = data['COMMON_SETTINGS']['sslpemkeyfile']
                connection = MongoClient(mongo_ip, int(mongo_port), ssl=True, ssl_certfile=sslpemkeyfile,
                                         ssl_ca_certs=ssl_ca_file)  # connecting to mongos instance
            else:
                connection = MongoClient(mongo_ip, int(mongo_port))  # connecting to mongos instance
        except:
            logging.error('Cannot connect to mongo instance. Cause: %s', sys.exc_info()[1])
            sys.exit(5)
    elif "mongoS" in data:
        mongos_instance_index = 0
        mongos_instance_count = len(data["mongoS"])
        for mongos_instance in data["mongoS"]:
            mongo_ip = mongos_instance["mongoS" + str(int(mongos_instance_index + 1))]["IP_Address"]
            mongo_port = mongos_instance["mongoS" + str(int(mongos_instance_index + 1))]["port"]
            logging.info("Trying to connect to mongo instance %s:%s", mongo_ip, mongo_port)
            try:
                if SSL == 'Y':
                    ssl_ca_file = data['COMMON_SETTINGS']['sslCAFile']
                    sslpemkeyfile = data['COMMON_SETTINGS']['sslpemkeyfile']
                    connection = MongoClient(mongo_ip, int(mongo_port), ssl=True, ssl_certfile=sslpemkeyfile,
                                             ssl_ca_certs=ssl_ca_file)  # connecting to mongos instance
                    break
                else:
                    connection = MongoClient(mongo_ip, int(mongo_port))  # connecting to mongos instance
                    break
            except:
                mongos_instance_index += 1
                logging.warning("Cannot connect to mongos instance %s:%s", mongo_ip, mongo_port)
                if mongos_instance_index < mongos_instance_count:
                    logging.info('Trying with next mongo instance')
                else:
                    logging.error("Cannot connect to mongos instance")
                    sys.exit(5)
    elif "MONGOS_CONFIG" in data:
        mongo_ip = data["MONGOS_CONFIG"]["IP"]
        mongo_port = data["MONGOS_CONFIG"]["PORT"]
        logging.info("Trying to connect to mongo instance %s:%s", mongo_ip, mongo_port)
        try:
            if SSL == 'Y':
                ssl_ca_file = data['COMMON_SETTINGS']['sslCAFile']
                sslpemkeyfile = data['COMMON_SETTINGS']['sslpemkeyfile']
                connection = MongoClient(mongo_ip, int(mongo_port), ssl=True, ssl_certfile=sslpemkeyfile,
                                         ssl_ca_certs=ssl_ca_file)  # connecting to mongos instance
            else:
                connection = MongoClient(mongo_ip, int(mongo_port))  # connecting to mongos instance
        except:
            logging.error('Cannot connect to mongo instance. Cause: %s', sys.exc_info()[1])
            sys.exit(5)
    else:
        connection = None
        logging.error("Cannot find key 'mongoS' or 'mongo' in configuration file")
        sys.exit(5)
    return connection