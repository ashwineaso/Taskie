__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll

response = {}
data = {}
taskObj = Collection()
userObj = Collection()

def addNewTask():
	
	obj = request.json
	try:
		taskObj.owner = obj["owner"]
		taskObj.priority = obj["priority"]
		taskObj.name = obj["name"]
		taskObj.description = obj["description"]
		taskObj.dueDateTime = obj["dueDateTime"]
		taskObj.collaborators = obj["collaborators"]
		taskObj.status = obj["status"]
		task = bll.addNewTask(taskObj)
		data["task"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def editTask():
	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.priority = obj["priority"]
		taskObj.name = obj["name"]
		taskObj.description = obj["description"]
		taskObj.dueDateTime = obj["dueDateTime"]
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
		taskObj.dateTime = obj["dateTime"]
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
		taskObj.statusDateTime = obj["statusDateTime"]
		task = bll.modifyCollStatus(taskObj)
		data["collaborator"] = task.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response
