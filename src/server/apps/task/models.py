__author__ = ["ashwineaso"]
from mongoengine import *
from apps.users.models import User
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper
from settings.constants import NORMAL_PRIORITY
import time

connect()

class Status(EmbeddedDocument):
	status = IntField()
	dateTime = LongField(default = time.time())

	def to_dict(self):
		return mongo_to_dict_helper(self)

class Collaborator(EmbeddedDocument):
	user = ReferenceField(User)
	status = EmbeddedDocumentField(Status)
	startTime = LongField(required = False)
	endTime = LongField(required = False)

	def to_dict(self):
		return mongo_to_dict_helper(self)


class TaskGroup(Document):
	owner = ReferenceField(User)
	members = ListField(ReferenceField(User), required = False)
	title = StringField()

	def to_dict(self):
		return mongo_to_dict_helper(self)

class Task(Document):
	owner = ReferenceField(User)
	collaborators = ListField(EmbeddedDocumentField(Collaborator))
	priority = IntField(default = NORMAL_PRIORITY)
	name = StringField()
	description = StringField(required = False)
	dueDateTime = LongField(required = False)
	status = EmbeddedDocumentField(Status)
	isgroup = BooleanField(default = False)
	group = ReferenceField(TaskGroup, required = False)

	def to_dict(self):
		return mongo_to_dict_helper(self)