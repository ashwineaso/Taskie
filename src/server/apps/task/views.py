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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response["status"] = RESPONSE_SUCCESS
		response["data"] = bll.taskToDictConverter(task)
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def deleteTask():
	response = {}
	data = {}

	obj = request.json
	try:
		taskObj.id = obj["id"]
		taskObj.access_token = obj["access_token"]
		if checkAccessTokenValid(taskObj) is True:
			flag = bll.deleteTask(taskObj)
		if flag is True:
			response["status"] = RESPONSE_SUCCESS
			response["message"] = "Task Deleted"
		else
			response["status"] = RESPONSE_FAILED
			response["message "] = "Deletion Failed"
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, 'code'):
			response["code"] = e.code
	return response


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
	response = {}
	data = {}

	obj = request.json
	# try:
	taskObj.id = obj["id"]
	taskObj.access_token = obj["access_token"]
	if checkAccessTokenValid(taskObj) is True:
		flag = bll.buzzCollaborators(taskObj)
	if (flag):
		response["status"] = RESPONSE_SUCCESS
	else:
		response["status"] = RESPONSE_FAILED
	# except Exception as e:
	# 	response["status"] = RESPONSE_FAILED
	# 	response["message"] = str(e)
	# 	if hasattr(e, 'code'):
	# 		response["code"] = e.code
	return response