from mongoengine import *
from apps.users.models import User
from settings.methods import connect

connect()

class Status(EmbeddedDocument):
	status = IntField()
	dateTime = DateTimeField()

class Collaborator(EmbeddedDocument):
	user = ReferenceField(User)
	status = EmbeddedDocument(Status)
	startTime = DateTimeField()
	endTime = DateTimeField()

class Task(Document):
	owner = ReferenceField(User)
	collaborators = ListField(EmbeddedDocument(Collaborator))
	priority = IntField()
	name = StringField()
	description = StringField()
	dueDateTime = DateTimeField()
	status = EmbeddedDocument(Status)

__author__ = ["ashwineaso"]