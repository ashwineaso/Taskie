__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll
from apps.users.bll import checkAccessTokenValid
import json


taskObj = Collection()
userObj = Collection()
groupObj = Collection()


def addNewTask():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []

	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
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
			#Validate access_token and continue process
			if checkAccessTokenValid(taskObj) is True:
				task = bll.addNewTask(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def editTask():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			taskObj.priority = obj["priority"]
			taskObj.name = obj["name"]
			taskObj.description = obj["description"]
			taskObj.dueDateTime = obj["dueDateTime"]
			if checkAccessTokenValid(taskObj) is True:
				task = bll.editTask(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def addCollaborators():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			taskObj.collaborators = obj["collaborators"]
			if checkAccessTokenValid(taskObj) is True:
				task = bll.addCollaborators(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def remCollaborators():
	jjsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			taskObj.collaborators = obj["collaborators"]
			if checkAccessTokenValid(taskObj) is True:
				task = bll.remCollaborators(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def modifyTaskStatus():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			taskObj.status = obj["status"]
			if checkAccessTokenValid(taskObj) is True:
				task = bll.modifyTaskStatus(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def modifyCollStatus():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			taskObj.email = obj["collemail"]
			taskObj.collstatus = obj["collstatus"]
			if checkAccessTokenValid(taskObj) is True:
				task = bll.modifyCollStatus(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def syncTask():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj
			if checkAccessTokenValid(taskObj) is True:
				task = bll.syncTask(taskObj)
			response["status"] = RESPONSE_SUCCESS
			response["data"] = bll.taskToDictConverter(task)
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def deleteTask():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj["id"]
			if checkAccessTokenValid(taskObj) is True:
				flag = bll.deleteTask(taskObj)
			if flag is True:
				response["status"] = RESPONSE_SUCCESS
				response["message"] = "Task Deleted"
			else:
				response["status"] = RESPONSE_FAILED
				response["message "] = "Deletion Failed"
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, 'code'):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def syncAllTasks():
	response = {}
	data = {}

	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.access_token = obj["access_token"]
		if checkAccessTokenValid(taskObj) is True:
			task_list = bll.syncAllTasks(taskObj)
		response["status"] = RESPONSE_SUCCESS
		response["data"] = []
		for each_task in task_list:
			response["data"].append(bll.taskToDictConverter(each_task))
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def buzzCollaborators():
	jsonResponse = {}

	jsonObj = request.json
	jsonResponse["data"] = []
	taskObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			taskObj.id = obj
			if checkAccessTokenValid(taskObj) is True:
				flag = bll.buzzCollaborators(taskObj)
			if (flag):
				response["status"] = RESPONSE_SUCCESS
			else:
				response["status"] = RESPONSE_FAILED
		except Exception as e:
			response["status"] = RESPONSE_FAILED
			response["message"] = str(e)
			if hasattr(e, 'code'):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse