__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll
from apps.users.bll import checkAccessTokenValid

response = {}
data = {}
taskObj = Collection()
userObj = Collection()
groupObj = Collection()

def addNewTask():
	
	obj = request.json
	# try:
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
	taskObj.isgroup = obj["isgroup"]
	try:
		taskObj.group = obj["groupId"]
	except KeyError:
		taskObj.group = ''
	if (checkAccessTokenValid(taskObj)):
		task = bll.addNewTask(taskObj)
	data["task"] = task.to_dict()
	response["status"] = RESPONSE_SUCCESS
	response["data"] = data
	# except Exception as e:
	# 	response["status"] = RESPONSE_FAILED
	# 	response["message"] = str(e)
		# response["code"] = e.code
	return response


def editTask():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.priority = obj["priority"]
		taskObj.name = obj["name"]
		try:
			taskObj.description = obj["description"]
		except KeyError:
			taskObj.description = ''
		try:
			taskObj.dueDateTime = obj["dueDateTime"]
		except Exception:
			taskObj.dueDateTime = ''
		task = bll.editTask(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def addCollaborators():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.collaborators = obj["collaborators"]
		task = bll.addCollaborators(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def remCollaborators():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.collaborators = obj["collaborators"]
		task = bll.remCollaborators(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def modifyTaskStatus():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.status = obj["status"]
		task = bll.modifyTaskStatus(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def modifyCollStatus():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.email = obj["collemail"]
		taskObj.collstatus = obj["collstatus"]
		task = bll.modifyCollStatus(taskObj)
		data["collaborator"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def createGroup():
	obj = request.json
	try:
		groupObj.owner = obj["ownerId"]
		groupObj.title = obj["title"]
		group = bll.createGroup(groupObj)
		data["group"] = group.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def addGroupMembers():
	obj = request.json
	try:
		groupObj.id = obj["groupId"]
		groupObj.member = obj["memberId"]
		group = bll.addGroupMembers()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response