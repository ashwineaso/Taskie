__author__ = ["ashwineaso"]
from . import dal
from settings.exceptions import TaskWithIDNotFound
from apps.users import bll as userbll
from settings.altEngine import Collection
from settings.constants import GCMPost, TOKEN_GCM_REGISTRATION_IDS, UrlPostThread
from datetime import datetime
from bottle import request


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
	pushSyncTaskNotification(task)
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
	pushSyncTaskNotification(task)
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
	pushSyncTaskNotification(task)
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

	task = dal.remCollaborators(taskObj)
	pushSyncTaskNotification(task)
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
	pushSyncTaskNotification(task)
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
	pushSyncTaskNotification(task)
	return task


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
	return group 


def syncTask(taskObj):
	"""
	Sync / retrieve as task whose id is provided
	"""
	
	task = dal.syncTask(taskObj)
	## Modify task to include only all essential details
	return task


def pushSyncTaskNotification(taskObj):
	"""
	Initiates a server side push to all the collaborators
	of the task
	"""

	#Create a GCMPost object for Android Push
	androidPush = GCMPost()
	#List to store GCM ids
	androidPayload = []
	userObj = Collection()

	#for each collaborator of the task
	task = dal.getTaskById(taskObj)
	for coll in task.collaborators:
		if not coll.user.serverPushId in androidPayload:
			androidPayload.append(str(coll.user.serverPushId))

	#for the owner of the task
	androidPayload.append(str(task.owner.serverPushId))
	

	if len(androidPayload) > 0:
		androidPush.payload[TOKEN_GCM_REGISTRATION_IDS] = androidPayload
		androidPush.payload["data"] = {"syncTask" : True, "taskId" : str(taskObj.id)}
		print androidPush.payload

		#Create UrlPoster Thread for GCM Push Start Thread
		gcmPostThread = UrlPostThread(
									threadID = 1,
									name = 'gcmPostThread',
									postObj = androidPush
									)
		gcmPostThread.start()


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
	taskie["isgroup"] = task.isgroup
	taskie["group"] = task.group

	#Retrieve owner information
	taskie["owner"] = str(task.owner.id)
	#Collaborator informaiton
	taskie["collaborators"] = []
	for each_user in task.collaborators:
		status["status"] = each_user.status.status
		status["dateTime"] = each_user.status.dateTime
		coll["id"] = str(each_user.user.id)
		coll["name"] = each_user.user.name
		coll["status"] = status.copy()
		coll["startTime"] = each_user.startTime
		coll["endTime"] = each_user.endTime
		taskie["collaborators"].append(coll.copy())
	return taskie