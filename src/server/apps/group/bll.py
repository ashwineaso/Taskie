__author__ = ["ashwineaso"]
from . import dal
from settings.exceptions import TaskWithIDNotFound
from apps.users import bll as userbll
from settings.altEngine import Collection, SyncClass
from settings.constants import GCMPost, TOKEN_GCM_REGISTRATION_IDS, UrlPostThread
from datetime import datetime
from bottle import request


def createGroup(groupObj):
	"""
	Create a new group
	
	:type groupObj : object
	:param groupObj : An instance with the following attributes
						ownerId - userId of the creator/ member
						title - name of the groupObj
	:return An instance of the Group class
	"""
	group = dal.createGroup(groupObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Group", str(group.id))
	#pushSyncNotification(syncObj)
	return group


def addGroupMembers(groupObj):
	"""
	Add members to a groupObj

	:type groupObj : object
	:param groupObj : An instance of Collection with the following attributes
						id - id of the TaskGroup
						member - list of members to be added to the group
	:return : An instance of the Group class
	"""

	group = dal.addGroupMembers(groupObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Group", str(group.id))
	#pushSyncNotification(syncObj)
	return group



def remGroupMembers(groupObj):
	"""
	Remove memberd from a group

	:type groupObj : object
	:param groupObj : An instance of Collection with the following attributes
						id - id of the TaskGroup
						member - list of members to be added to the group
	:return : An instance of the Group class
	"""

	group = dal.remGroupMembers(groupObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Group", str(group.id))
	#pushSyncNotification(syncObj)
	return group


def addNewTask(taskObj):
	"""
	Adds a new task to the task list

	:type taskObj : object
	:para. taskObj : An object with the following attributes
			owner (objectId),
			collaborators,
			priority,
			name,
			description,
			dueDateTime,
			status
			isgroup
			group
	:return an object of the task class.
	"""
	#Add a task to the servers task list
	task = dal.addNewTask(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def taskToDictConverter(task):
	"""
	Convert the incoming GroupTask object into JSON Serializable dict format
	only including the essential details

	::type task : instance of Task class
	::param task : attributes of GroupTask, Collaborator, Status and User Classes
	::return taskie : dictionary
	"""
	taskie = {}
	coll = {}
	status = {}

	## Modify task to include only all essential details
	taskie["id"] = str(task.id)
	taskie["name"] = task.name
	taskie["description"] = task.description
	taskie["dueDateTime"] = task.dueDateTime
	taskie["priority"] = task.priority
	#Setting the status
	status["status"] = task.status.status
	status["dateTime"] = task.status.dateTime
	taskie["status"] = status.copy()

	#GroupTask attributes
	taskie["collaborator_count"] = task.collaborator_count
	taskie["availability"] = task.availability
	#Retrieve owner information
	taskie["owner"] = str(task.owner.id)
	#Collaborator informaiton
	taskie["collaborators"] = []
	for each_user in task.collaborators:
		status["status"] = each_user.status.status
		status["dateTime"] = each_user.status.dateTime
		coll["id"] = str(each_user.user.id)
		coll["name"] = each_user.user.name
		coll["email"] = each_user.user.email
		coll["status"] = status.copy()
		coll["startTime"] = each_user.startTime
		coll["endTime"] = each_user.endTime
		taskie["collaborators"].append(coll.copy())
	return taskie