__author__ = ["ashwineaso"]
from mongoengine import *
from apps.task.models import Task
from apps.users.models import User
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper

connect()

class GroupTask(Task):
	#Inherited from Task Document in task app
	collaborator_count = IntField(default = 1)
	availability = BooleanField(default = True)

	def __init__(self, owner, collaborators, priority, name, description, dueDateTime, status, collaborator_count):
		super(GroupTask, self).__init__(owner, 
										collaborators, 
										priority, 
										name, 
										description, 
										dueDateTime, 
										status)
		self.collaborator_count = collaborator_count


	def to_dict(self):
		return mongo_to_dict_helper(self)



class TaskGroup(Document):
	owner = ReferenceField(User)
	members = ListField(ReferenceField(User), required = False)
	title = StringField()
	task_list = ListField(ReferenceField(GroupTask))

	def to_dict(self):
		return mongo_to_dict_helper(self)