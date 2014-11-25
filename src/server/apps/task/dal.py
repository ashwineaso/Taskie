__author__ = ["ashwineaso"]
from apps.task.models import *
from apps.users import bll as userbll
from settings.altEngine import Collection
import datetime

def addNewTask(taskObj):
	"""
	Creates a task

	"""

	#Assigning initial status to each task
	taskObj.status = Status(
					status = 0,
					dateTime = datetime.datetime.now()
					)

	#Define an emptly list to include all the user objects
	my_objects = []
	userObj = Collection()
	
	#Creating the list of collaborators
	for val in taskObj.collaborators:
		userObj.email = val
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.status))
	
	#Create a task with the necessary data.
	task = Task(
		owner = taskObj.owner,
		collaborators = my_objects,
		priority = taskObj.priority,
		name = taskObj.name,
		description = taskObj.description,
		dueDateTime = taskObj.dueDateTime,
		status = taskObj.status
		).save()
	return task