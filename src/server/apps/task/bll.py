__author__ = ["ashwineaso"]
from . import dal
from settings.exceptions import TaskWithIDNotFound
from apps.users import bll as userbll
from settings.altEngine import Collection
from settings.constants import GCMPost, TOKEN_GCM_REGISTRATION_IDS, UrlPostThread
from datetime import datetime


def addNewTask(taskObj):
	"""
	Adds a new task to the task list

	:type taskObj : object
	:para. taskObj : An object with the following attributes
			owner,
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
	taskObj.dueDateTime = datetime.now()
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

	#Search task using id
	try:
		taskObj.task = dal.getTaskByID(taskObj)
		#Update database with task information
		task = dal.editTask(taskObj)
		pushSyncTaskNotification(task)
		return task
	except TaskWithIDNotFound as e:
		raise e


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


def pushSyncTaskNotification(taskObj):
	"""
	Initiates a server side push to all the collaborators
	of the task
	"""

	#Create a GCMPost object for Android Push
	androidPush = GCMPost()
	#List to store GCM ids
	androidPayload = []

	#for each collaborator of the task
	for coll in taskObj.collaborator:
		if not coll.serverPushId in androidPayload:
			androidPayload.append(str(coll.serverPushId))

	#for the owner of the task
	androidPayload.append(str(taskObj.owner.serverPushId))

	if len(androidPayload) > 0:
		androidPush.payload[TOKEN_GCM_REGISTRATION_IDS] = androidPayload
		androidPush.payload["data"] = {"syncTask" : True, "taskId" : "taskObj.id"}

		#Create UrlPoster Thread for GCM Push Start Thread
		gcmPostThread = UrlPostThread(
									threadID = 1,
									name = 'gcmPostThread',
									postObj = androidPush
									)
		gcmPostThread.start()