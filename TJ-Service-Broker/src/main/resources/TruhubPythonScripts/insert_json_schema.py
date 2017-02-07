###############################################################################################
###############################################################################################
###	    Scriprt for inserting/updating json schema                                          ###
###     author : Jayasree C J (368140)       Date: 13-Oct-2014                               ###
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

import trubox_db_connection #tool for connecting to mongoDB


#####################################################################
####                  Fuction Definition                         ####
####   Function Name : insert_json_schema                        ####
####   Argument1 : db_name                                       ####
####   Argument2 : collection_name (Optional)                    ####
####   Argument3 : is_db_specific (Optional)                     ####
####   This function is responsible for inserting json schema    ####
#####################################################################
def insert_json_schema(db_name,collection_name="",is_db_specific = False):
    #Checking if json schema is to be inserted to MASTER_DB
    if db_name == 'MASTER_DB':
        if collection_name == "":
            collection_list = data[db_name]['COLLECTIONS'] #Get the collection names for which json schema has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        #Iterating collection_list
        for collection in collection_list:
            logging.info('Inserting json schema for %s in %s', collection , db_name )
            collection_name = data[collection]['COLLECTION_NAME'] #Get the collection name
            schema = data[collection]['JSON_SCHEMA'] #Get the json schema for collection
            option = data[collection]['OPTION'] #Get the options defined for schema
            #Connecting to MASTER_DB
            db = connection['admin']
            try:
                db.authenticate("ZTAxYjZkYTgtZmY1ZS00YjdkLTgwMjItOGE4YWIwZjIzNTlj","qw5kjwg6wv") #Authenticating as read write user
            except:
                logging.info('authenticate for mongodb3.0 failed %s',sys.exc_info()[1])

            db = connection[db_name]

            #Inserting collection json schema
            try:
                category = data[collection]['CATEGORY_NAME']
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "CATEGORY_NAME": category, "OPTION" : option})
            except:
                db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "OPTION" : option})
            logging.info('Inserted JSON_SCHEMA for %s', collection_name)
            logging.info('Creating index for %s', collection_name)
            #Getting the indexes for collection
            try:
                indexes = data[collection]['INDEXES'].split()
            except:
                logging.info('No index specification found for %s',collection_name)
                continue
            for index in indexes: #Iterating indexes
                index_elements = data[collection][index].split()
                index_array = []
                #db[collection_name].drop_indexes()
                for index_element in index_elements:
                    field_spec = index_element.split(',')
                    field_name = field_spec[0] #Get attribute name
                    field_type = field_spec[1] #Get type of index
                    if (field_type == '1') or (field_type == '-1'):
                        field_type = int(field_type) #Convert index type to int in case of '1'
                    index_object = (field_name,field_type)
                    index_array.append(index_object)
                option_definition = index + '_OPTIONS'
                options = data[collection][option_definition] #Getting indexing options
                options_json = options
                db[collection_name].ensure_index(index_array,**options_json) #Ensuring index for collection
            logging.info('Completed creating index for %s', collection_name)
    elif db_name in ['Trumobi']:
        db = connection['MASTER_DB']
        #company_list = db['tenant_master'].aggregate([{'$project':{'TENANT_ID':1,'_id':0}},{'$group':{'_id':'COMPANY_IDS','COMPANY':{'$addToSet':'$TENANT_ID'}}}])['result'][0]['COMPANY'] #Getting list of companies from company collection
        company_list = db['tenant'].aggregate([{'$project':{'tenant_id':1,'_id':0}},{'$group':{'_id':'COMPANY_IDS','COMPANY':{'$addToSet':'$tenant_id'}}}]).next()['COMPANY'] #Getting list of companies from company collection
        if collection_name == "":
            collection_list = data[db_name]['COLLECTIONS'] #Get the collection names for which json schema has to be inserted
        else:
            collection_list = [collection_name] #If collection name is specified collection_list will hold the name of just one collection
        #Iterating company
        for company_id in company_list:
            #Framing the DB name
            #id_db_name = str(int(company_id)) + '_' + db_name
            id_db_name = str(int(company_id)) + '_' + db_name
            #Iterating collection_list
            for collection in collection_list:
                logging.info('Inserting json schema for %s in %s', collection , id_db_name )
                collection_name = data[collection]['COLLECTION_NAME'] #Get the collection name
                schema = data[collection]['JSON_SCHEMA'] #Get the json schema for collection
                option = data[collection]['OPTION'] #Get the options defined for schema
                #Connecting to the db
                db = connection[id_db_name]
                #Inserting collection json schema
                try:
                    category = data[collection]['CATEGORY_NAME']
                    db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                    db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "CATEGORY_NAME": category, "OPTION" : option})
                except:
                    db[json_schema_collection_name].remove({"COLLECTION_NAME" : collection_name})
                    db[json_schema_collection_name].insert({"COLLECTION_NAME" : collection_name , "JSON_SCHEMA" : schema, "OPTION" : option})
                logging.info('Inserted JSON_SCHEMA for %s', collection_name)
                #Getting the indexes for collection
                try:
                    indexes = data[collection]['INDEXES'].split()
                except:
                    logging.info('No index specification found for %s',collection_name)
                    continue
                for index in indexes: #Iterating indexes
                    index_elements = data[collection][index].split()
                    index_array = []
                    #db[collection_name].drop_indexes()
                    for index_element in index_elements:
                        print "index_element"
                        print index_element
                        field_spec = index_element.split(',')
                        print field_spec
                        field_name = field_spec[0] #Get attribute name
                        print field_name
                        field_type = field_spec[1] #Get type of index
                        print field_type
                        if (field_type == '1') or (field_type == '-1'):
                            field_type = int(field_type) #Convert index type to int in case of '1'
                        index_object = (field_name,field_type)
                        index_array.append(index_object)
                    option_definition = index + '_OPTIONS'
                    options = data[collection][option_definition] #Getting indexing options
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
    logging.basicConfig(filename='trubox.log',level=default_level)

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
    # tj_read_write_user_name = trubox_db_connection.tj_read_write_user_name #Contains the DB read write user name
    # tj_read_write_user_pwd = trubox_db_connection.tj_read_write_user_pwd #Contains the DB read write user password
    company_collection = trubox_db_connection.company_collection #Contains the name of COMPANY collection
    json_schema_collection_name = trubox_db_connection.json_schema_collection_name #Contains the name of JSON SCHEMA collection (COLLECTION_JSON_SCHEMA)
    #SSL = trubox_db_connection.SSL
except:
    logging.error('Error reading configuration values. Cause: %s',sys.exc_info()[1] )
    sys.exit(4) #In case it can't read any of the config file it will throw error

logging.info('Successfully read from configuration file')

connection = trubox_db_connection.create_connection()

# get the arguments
argument_Length = len(sys.argv)

if argument_Length < 2:
    logging.info('Inserting json schema for MASTER_DB')
    insert_json_schema('MASTER_DB') #Inserting json schema for MASTER_DB
    logging.info('Inserted json schema for MASTER_DB')
    logging.info('Inserting json schema for Trumobi')
    insert_json_schema('Trumobi') #Inserting json schema for META_DB
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
