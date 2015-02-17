import os
from mongoengine import *

#Project root directory
PROJECT_ROOT = os.path.realpath(os.path.dirname(os.path.dirname(__file__)))

#Version of altEngine
VERSION = 0.1

#API Status responses.
RESPONSE_SUCCESS = "success"
RESPONSE_FAILED = "failed"


class Collection():
	pass


class SyncClass():
    def __init__(self, datatype , type_id ):
        self.datatype = datatype
        self.id = type_id

    def to_dict(self):
        return {"datatype":self.datatype, "id":self.id}



def mongo_to_dict_helper(obj):
    """
    Returns a serializable dictionary of the object.

    :param obj: Object
    """
    return_data = []
    for field_name in obj._fields:

        if field_name in ("id",):
            return_data.append(("id", str(obj._data[field_name])))

        data = obj._data[field_name]

        if (field_name in ("image")):
            pass
        elif (field_name in ("contentType")):
            pass
        elif isinstance(obj._fields[field_name], StringField):
            return_data.append((field_name, str(data)))
        elif isinstance(obj._fields[field_name], FloatField):
            return_data.append((field_name, float(data)))
        elif isinstance(obj._fields[field_name], IntField):
            return_data.append((field_name, int(data)))
        elif (field_name in ("group")):
            pass
        elif isinstance(obj._fields[field_name], ListField):
            pass
        elif isinstance(obj._fields[field_name], LongField):
            return_data.append((field_name, str(data)))
        elif isinstance(obj._fields[field_name], BooleanField):
            return_data.append((field_name, data))
        elif isinstance(obj._fields[field_name], EmbeddedDocumentField):
            return_data.append((field_name, mongo_to_dict_helper(data)))
        elif isinstance(obj._fields[field_name], ReferenceField):
            return_data.append((field_name, mongo_to_dict_helper(data)))
        elif isinstance(obj._fields[field_name], ImageField):
            pass
        else:
            pass
    return dict(return_data)

__author__ = ['mahesmohan','ashwineaso']