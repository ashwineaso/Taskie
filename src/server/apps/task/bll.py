__author__ = ["ashwineaso"]
from . import dal
from settings.exceptions import TaskWithIDNotFound
from apps.users import bll as userbll
from settings.altEngine import Collection, SyncClass
from settings.gcmpush import *
from datetime import datetime
from bottle import request


def addNewTask(taskObj):
	"""
	Adds a new task to the task list

	:type taskObj : object
	:param taskObj : An object with the following attributes
			owner (objectId),
			collaborators,
			priority,
			name,
			description,
			dueDateTime,
			status

	:return an object of the task class.
	"""
	#Add a task to the servers task list
	task = dal.addNewTask(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def editTask(taskObj):
	"""
	Edits and existing taskObj

	:type taskObj : object
	:para taskObj : An object with the following attributes
			_id,
			name,
			description,
			dueDateTime,
			priority
	:return : an object of task class
	"""
	#Update database with task information
	task = dal.editTask(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def addCollaborators(taskObj):
	"""
	Add collaborator to a taskObj

	:type taskObj : object
	:para taskObj : An object with the following attributes
			_id,
			collaborators
	:return : An object of task class
	"""

	task = dal.addCollaborators(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def remCollaborators(taskObj):
	"""
	Add collaborator to a taskObj

	:type taskObj : object
	:para taskObj : An object with the following attributes
			_id,
			collaborators
	:return : An object of task class
	"""

	syncObj = SyncClass("CollRemoved", str(taskObj.id))
	pushSyncNotification(syncObj, taskObj)

	task = dal.remCollaborators(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def modifyTaskStatus(taskObj):
	"""
	Modfiy the status of the existing taskObj

	type taskObj: object
	:param taskObj: An instance with the following attributes
			id
			status
	:return An instance of the Task class

	"""

	task = dal.modifyTaskStatus(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task


def modifyCollStatus(taskObj):
	"""
	Modify the status of the collaborator

	:type taskObj : object
	:param taskObj : An instance with the following attributes
					id - id of the task
					collemail - email of the collaborator
					collstatus - new status of the collaborator
					statusDateTime - dateTime of status update
	:return An instance of the Collaborator class
	"""

	task = dal.modifyCollStatus(taskObj)
	#Send message to GCM server to notify collaborators of task
	syncObj = SyncClass("Task", str(task.id))
	pushSyncNotification(syncObj)
	return task



def syncTask(taskObj):
	"""
	Sync / retrieve as task whose id is provided
	"""
	
	task = dal.syncTask(taskObj)
	## Modify task to include only all essential details
	return task


def deleteTask(taskObj):
	"""Delete a task and update all collaborators about the status"""
	taskObj = Collection()
	taskObj.collaborators = dal.getTaskById(taskObj.id)
	flag = dal.deleteTask(taskObj)
	if flag is True:
		syncObj = SyncClass("Deleted", str(task.id))
		pushSyncNotification(syncObj, taskObj)
	return flag


def syncAllTasks(taskObj):
	"""
	Sync all tasks of user of which is he owner and collaborator
	"""
	task_list = dal.syncAllTasks(taskObj)
	return task_list


def buzzCollaborators(taskObj):
	"""
	Send a buzz to all the collaborators
	"""

	#Send message to GCM server to notify collaborators of buzz
	syncObj = SyncClass("Buzz", str(taskObj.id))
	pushSyncNotification(syncObj)
	return True

def taskToDictConverter(task):
	"""
	Convert the incoming Task object into JSON Serializable dict format
	only including the essential details

	::type task : instance of Task class
	::param task : attributes of Task, Collaborator, Status and User Classes
	::return taskie : dictionary
	"""
	taskie = {}
	coll = {}
	status = {}
	owner = {}
	userObj = Collection()

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

	#Retrieve owner information
	owner["id"] = str(task.owner.id)
	owner["name"] = task.owner.name
	owner["email"] = task.owner.email
	taskie["owner"] = owner

	#Collaborator informaiton
	taskie["collaborators"] = []
	for each_user in task.collaborators:
		status["status"] = each_user.status.status
		status["dateTime"] = each_user.status.dateTime
		coll["id"] = str(each_user.user.id)
		coll["name"] = str(each_user.user.name)
		coll["email"] = str(each_user.user.email)
		coll["status"] = status.copy()
		coll["startTime"] = each_user.startTime
		coll["endTime"] = each_user.endTime
		taskie["collaborators"].append(coll.copy())
	return taskie