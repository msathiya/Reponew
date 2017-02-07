###############################################################################################
###############################################################################################
###	    Scriprt for inserting/updating json schema                                          ###
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

import tj_db_connection #tool for connecting to mongoDB


#####################################################################
####                  Fuction Definition                         ####
####   Function Name : insert_json_schema                        ####
####   Argument1 : db_name                                       ####
####   Argument2 : collection_name (Optional)                    ####
####   Argument3 : is_db_specific (Optional)                     ####
####   This function is responsible for inserting json schema    ####
#####################################################################
def insert_json_schema(db_name, collection_name="", is_db_specific=False):
    #Checking if json schema is to be inserted to MASTER_DB
    if db_name == 'MASTER_DB':
        if collection_name == "":
            collection_list = data[db_name]['COLLECTIONS'] #Get the collection names for which json schema has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        #Iterating collection_list
        for collection in collection_list:
            logging.info('Inserting json schema for %s in %s', collection , db_name )
            collection_name = data[db_name]['SCHEMA'][collection]['COLLECTION_NAME'] #Get the collection name
            schema = data[db_name]['SCHEMA'][collection]['JSON_SCHEMA'] #Get the json schema for collection
            option = data[db_name]['SCHEMA'][collection]['OPTION'] #Get the options defined for schema
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
			#Inserting collection json schema
            try:
                category = data[db_name]['SCHEMA'][collection]['CATEGORY_NAME']
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "CATEGORY_NAME": category, "OPTION" : option})
            except:
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "OPTION" : option})
            logging.info('Inserted JSON_SCHEMA for %s', collection_name)
            logging.info('Creating index for %s', collection_name)
            #Getting the indexes for collection
            try:
                indexes = data[db_name]['SCHEMA'][collection]['INDEXES'].split()
            except:
                logging.info('No index specification found for %s',collection_name)
                continue
            #db[collection_name].drop_indexes()
            for index in indexes: #Iterating indexes
                index_elements = data[db_name]['SCHEMA'][collection][index].split()
                index_array = []
                for index_element in index_elements:            
                    field_spec = index_element.split(',')
                    field_name = field_spec[0] #Get attribute name
                    field_type = field_spec[1] #Get type of index
                    if (field_type == '1') or (field_type == '-1'):
                         field_type = int(field_type) #Convert index type to int in case of '1'
                    index_object = (field_name,field_type)
                    index_array.append(index_object)
                option_definition = index + '_OPTIONS'
                options = data[db_name]['SCHEMA'][collection][option_definition] #Getting indexing options
                options_json = options
                db[collection_name].ensure_index(index_array,**options_json) #Ensuring index for collection
            logging.info('Completed creating index for %s', collection_name)
    elif db_name in ['META_DB','SECURITY_DB','RULE_DB','CONTEXT_DB','NOTIFICATION_DB', 'APP_STORAGE_DB','APP_LOG_DB']:
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
        company_list = 	db['COMPANY'].aggregate([{'$project':{'COMPANY_ID':1,'_id':0}},{'$group':{'_id':'COMPANY_IDS','COMPANY':{'$addToSet':'$COMPANY_ID'}}}]).next()['COMPANY'] #Getting list of companies from company collection
        if collection_name == "":
            collection_list = data[db_name]['COLLECTIONS'] #Get the collection names for which json schema has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        #Iterating company
        for company_id in company_list:
        #Framing the DB name
            if db_name == 'EVENT_LOG_NON_TJ':
                id_db_name = str(int(company_id)) + '_EVENT_LOG_DB'
            else:
                id_db_name = str(int(company_id)) + '_' + db_name
			#Iterating collection_list
            for collection in collection_list:
                logging.info('Inserting json schema for %s in %s', collection , id_db_name )
                collection_name = data[db_name]['SCHEMA'][collection]['COLLECTION_NAME'] #Get the collection name
                schema = data[db_name]['SCHEMA'][collection]['JSON_SCHEMA'] #Get the json schema for collection
                option = data[db_name]['SCHEMA'][collection]['OPTION'] #Get the options defined for schema
				#Connecting to the db
                db = connection[id_db_name] 
				#Inserting collection json schema
                '''
                try:
                    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
                except:
                    logging.info('authenticate for mongodb3.0');
                    try:
                        db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
                    except:
                        logging.error('Error authenticating to %s. Cause: %s',id_db_name,  sys.exc_info()[1])   #Authentication failure
                        sys.exit(15)
                '''
                try:
                    category = data[db_name]['SCHEMA'][collection]['CATEGORY_NAME']
                    db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                    db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "CATEGORY_NAME": category, "OPTION" : option})
                except:
                    db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                    db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "OPTION" : option})
                logging.info('Inserted JSON_SCHEMA for %s', collection_name)
				#Getting the indexes for collection
                try:
                    indexes = data[db_name]['SCHEMA'][collection]['INDEXES'].split()
                except:
                    logging.info('No index specification found for %s',collection_name)
                    continue
                #db[collection_name].drop_indexes()
                for index in indexes: #Iterating indexes
                    logging.info("Creating index %s", index)
                    index_elements = data[db_name]['SCHEMA'][collection][index].split()
                    index_array = []
                    for index_element in index_elements:            
                        field_spec = index_element.split(',')
                        field_name = field_spec[0] #Get attribute name
                        field_type = field_spec[1] #Get type of index
                        if (field_type == '1') or (field_type == '-1'):
                             field_type = int(field_type) #Convert index type to int in case of '1'
                        index_object = (field_name,field_type)
                        index_array.append(index_object)
                    option_definition = index + '_OPTIONS'
                    options = data[db_name]['SCHEMA'][collection][option_definition] #Getting indexing options
                    options_json = options
                    db[collection_name].ensure_index(index_array,**options_json) #Ensuring index for collection
                logging.info('Completed creating index for %s', collection_name)
    elif is_db_specific == True:
        db_root = db_name.lstrip('0123456789_')
        if collection_name == "":
            collection_list = data[db_root]['COLLECTIONS']
        else:
            collection_list = [collection_name]
        for collection in collection_list: #Iterating collection_list
            logging.info('Inserting json schema for %s in %s', collection , db_name )
            collection_name = data[db_root]['SCHEMA'][collection]['COLLECTION_NAME'] #Get the collection name
            schema = data[db_root]['SCHEMA'][collection]['JSON_SCHEMA'] #Get the json schema for collection
            option = data[db_root]['SCHEMA'][collection]['OPTION'] #Get the options defined for schema
            db = connection[db_name]
            '''
            try:
                db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd) #Authenticating as read write user
            except:
                logging.info('authenticate for mongodb3.0');
                try:
                    db.authenticate(tj_read_write_user_name,tj_read_write_user_pwd,mechanism='SCRAM-SHA-1')
                except:
                    logging.error('Error authenticating to %s. Cause: %s',db_name,  sys.exc_info()[1])   #Authentication failure
                    sys.exit(15)
            '''
			#Inserting collection json schema
            try:
                category = data[db_root]['SCHEMA'][collection]['CATEGORY_NAME']
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "CATEGORY_NAME": category, "OPTION" : option})
            except:
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "OPTION" : option})
            logging.info('Inserted JSON_SCHEMA for %s', collection_name)
            #Getting the indexes for collection
            try:
                indexes = data[db_root]['SCHEMA'][collection]['INDEXES'].split()
            except:
                logging.info('No index specification found for %s',collection_name)
                continue
            #db[collection_name].drop_indexes()
            for index in indexes: #Iterating indexes
                index_elements = data[db_root]['SCHEMA'][collection][index].split()
                index_array = []
                for index_element in index_elements:            
                    field_spec = index_element.split(',')
                    field_name = field_spec[0] #Get attribute name
                    field_type = field_spec[1] #Get type of index
                    if (field_type == '1') or (field_type == '-1'):
                         field_type = int(field_type) #Convert index type to int in case of '1'
                    index_object = (field_name,field_type)
                    index_array.append(index_object)
                option_definition = index + '_OPTIONS'
                options = data[db_root]['SCHEMA'][collection][option_definition] #Getting indexing options
                options_json = options
                db[collection_name].ensure_index(index_array,**options_json) #Ensuring index for collection
            logging.info('Completed creating index for %s', collection_name)

## Initiating logging
default_logging_path = 'logging.json'
default_level = logging.INFO
env_key = 'LOG_CFG' ## This environment variable can be set to load corresponding logging doc

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

#Reading tru junction configuration file
logging.info('Reading tru junction config file')
try:
    current_working_directory = os.path.dirname(sys.argv[0])
    current_working_directory = os.path.abspath(current_working_directory)
    json_data = open(current_working_directory + '/json_schema.json')
    data = json.load(json_data)
    json_data.close()
except:
    logging.error('Cannot read from config file. Cause: %s',sys.exc_info()[1] )
    sys.exit(4)

try:
    tj_read_write_user_name = tj_db_connection.tj_read_write_user_name #Contains the DB read write user name
    tj_read_write_user_pwd = tj_db_connection.tj_read_write_user_pwd #Contains the DB read write user password
    tj_username= tj_db_connection.tj_username#Contains the tjuser username
    tj_userpwd = tj_db_connection.tj_userpwd#Contains the tjuser password
    company_collection = tj_db_connection.company_collection #Contains the name of COMPANY collection
    json_schema_collection_name = tj_db_connection.json_schema_collection_name #Contains the name of JSON SCHEMA collection (COLLECTION_JSON_SCHEMA)
    SSL = tj_db_connection.SSL
except:
    logging.error('Error reading configuration values. Cause: %s',sys.exc_info()[1] )
    sys.exit(4) #In case it can't read any of the config file it will throw error

logging.info('Successfully read from configuration file')	

connection = tj_db_connection.create_connection()
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

# get the arguments
argument_Length = len(sys.argv)

if argument_Length < 2:
    logging.info('Inserting json schema for MASTER_DB')
    insert_json_schema('MASTER_DB') #Inserting json schema for MASTER_DB
    logging.info('Inserted json schema for MASTER_DB')
    logging.info('Inserting json schema for META_DB')
    insert_json_schema('META_DB') #Inserting json schema for META_DB
    logging.info('Inserted json schema for META_DB')
    logging.info('Inserting json schema for SECURITY_DB')
    insert_json_schema('SECURITY_DB') #Inserting json schema for SECURITY_DB
    logging.info('Inserted json schema for SECURITY_DB')
    logging.info('Inserting json schema for RULE_DB')
    insert_json_schema('RULE_DB')  #Inserting json schema for RULE_DB
    logging.info('Inserted json schema for RULE_DB')
    logging.info('Inserting json schema for NOTIFICATION_DB')
    insert_json_schema('NOTIFICATION_DB')  #Inserting json schema for NOTIFICATION_DB
    logging.info('Inserted json schema for NOTIFICATION_DB')
    logging.info('Inserting json schema for APP_STORAGE_DB')
    insert_json_schema('APP_STORAGE_DB')  #Inserting json schema for APP_STORAGE_DB
    logging.info('Inserted json schema for APP_STORAGE_DB')
    logging.info('Inserting json schema for APP_LOG_DB')
    insert_json_schema('APP_LOG_DB')  #Inserting json schema for APP_LOG_DB
    logging.info('Inserted json schema for APP_LOG_DB')
    print 'Updated json schema for all dbs'
elif argument_Length == 2: #db name is given
    database_name = str(sys.argv[1]) #get the DB name from system argument
    if database_name[0].isdigit(): #Check if db_name is specific or general
        insert_json_schema(database_name,"", True) #Inserting json schema for specific database
        print 'Updated json schema for ' + database_name
    else:
        insert_json_schema(database_name) #Inserting json schema for database(s)
        print 'Updated json schema for ' + database_name
elif argument_Length == 3: #db and collection name is given
    database_name = str(sys.argv[1]) #get the DB name from system argument
    collection_name = str(sys.argv[2]) #get the collection name from system argument
    if database_name[0].isdigit(): 
        insert_json_schema(database_name,collection_name, True) #Inserting json schema for specific collection in specific database
        print 'Updated json schema for ' + collection_name + ' in ' + database_name
    else:
        insert_json_schema(database_name, collection_name) #Inserting json schema for specific collection in database(s)
        print 'Updated json schema for ' + collection_name + ' in ' + database_name
