__author__ = ["ashwineaso"]
from mongoengine import *
from apps.task.models import task
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper

connect()

class GroupTask(Task, EmbeddedDocument):
	collaborator_count = IntField(default = 1)

	def to_dict(self):
		return mongo_to_dict_helper(self)



class TaskGroup(Document):
	owner = ReferenceField(User)
	members = ListField(ReferenceField(User), required = False)
	title = StringField()
	task_list = ListField(EmbeddedDocumentField(GroupTask))

	def to_dict(self):
		return mongo_to_dict_helper(self)