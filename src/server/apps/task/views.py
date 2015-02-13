__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll
from apps.users.bll import checkAccessTokenValid


taskObj = Collection()
userObj = Collection()
groupObj = Collection()


def addNewTask():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.owner = obj["owner"]
		taskObj.name = obj["name"]
		try:
			taskObj.priority = obj["priority"]
		except KeyError:
			taskObj.priority = 1
		try:
			taskObj.description = obj["description"]
		except KeyError:
			taskObj.description = ''
		try:
			taskObj.dueDateTime = obj["dueDateTime"]
		except KeyError:
			taskObj.dueDateTime = 0
		taskObj.collaborators = obj["collaborators"]
		try:
			taskObj.isgroup = obj["isgroup"]
		except Exception:
			taskObj.isgroup = False
		try:
			taskObj.group = obj["groupId"]
		except KeyError:
			taskObj.group = ''
		if checkAccessTokenValid(taskObj) is True:
			task = bll.addNewTask(taskObj)
		taskie = bll.taskToDictConverter(task)
		data["task"] = taskie
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def editTask():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.id = obj["id"]
		taskObj.priority = obj["priority"]
		taskObj.name = obj["name"]
		taskObj.description = obj["description"]
		taskObj.dueDateTime = obj["dueDateTime"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.editTask(taskObj)
		taskie = bll.taskToDictConverter(task)
		data["task"] = taskie
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def addCollaborators():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.id = obj["id"]
		taskObj.collaborators = obj["collaborators"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.addCollaborators(taskObj)
		taskie = bll.taskToDictConverter(task)
		data["task"] = taskie
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def remCollaborators():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.id = obj["id"]
		taskObj.collaborators = obj["collaborators"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.remCollaborators(taskObj)
		taskie = bll.taskToDictConverter(task)
		data["task"] = taskie
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def modifyTaskStatus():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.id = obj["id"]
		taskObj.status = obj["status"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.modifyTaskStatus(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def modifyCollStatus():
	response = {}
	data = {}
	obj = request.json
	try:
		taskObj.access_token = obj["access_token"]
		taskObj.id = obj["id"]
		taskObj.email = obj["collemail"]
		taskObj.collstatus = obj["collstatus"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.modifyCollStatus(taskObj)
		data["collaborator"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def createGroup():
	response = {}
	data = {}
	obj = request.json
	try:
		groupObj.access_token = obj["access_token"]
		groupObj.owner = obj["ownerId"]
		groupObj.title = obj["title"]
		if checkAccessTokenValid(taskObj) is True:
			group = bll.createGroup(groupObj)
		data["group"] = group.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def addGroupMembers():
	response = {}
	data = {}
	obj = request.json
	try:
		groupObj.access_token = obj["access_token"]
		groupObj.id = obj["groupId"]
		groupObj.member = obj["memberId"]
		if checkAccessTokenValid(taskObj) is True:
			group = bll.addGroupMembers()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def syncTask():
	response = {}
	data = {}
	
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.access_token = obj["access_token"]
		if checkAccessTokenValid(taskObj) is True:
			task = bll.syncTask(taskObj)
		taskie = bll.taskToDictConverter(task)
		#add entire task to response data
		data["task"] = taskie
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		# response["code"] = e.code
	return response