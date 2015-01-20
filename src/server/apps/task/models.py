__author__ = ["ashwineaso"]
from mongoengine import *
from apps.users.models import User
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper
import datetime

connect()

class Status(EmbeddedDocument):
	status = IntField()
	dateTime = DateTimeField()

	def to_dict(self):
		return mongo_to_dict_helper(self)

class Collaborator(EmbeddedDocument):
	user = ReferenceField(User)
	status = EmbeddedDocumentField(Status)
	startTime = DateTimeField(required = False)
	endTime = DateTimeField(required = False)

	def to_dict(self):
		return mongo_to_dict_helper(self)


class Group(Document):
	owner = ReferenceField(User)
	members = ListField(ReferenceField(User))
	title = StringField()

	def to_dict(self):
		return mongo_to_dict_helper(self)

class Task(Document):
	owner = ReferenceField(User)
	collaborators = ListField(EmbeddedDocumentField(Collaborator))
	priority = IntField()
	name = StringField()
	description = StringField()
	dueDateTime = DateTimeField()
	status = EmbeddedDocumentField(Status)
	isgroup = BooleanField()
	group = ReferenceField(Group)

	def to_dict(self):
		return mongo_to_dict_helper(self)