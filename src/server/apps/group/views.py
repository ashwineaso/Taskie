__author__ = ["ashwineaso"]
from bottle import request
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
from . import bll
from apps.users.bll import checkAccessTokenValid


def createGroup():
	groupObj = Collection()
	response = {}
	data = {}
	obj = request.json
	try:
		groupObj.access_token = obj["access_token"]
		groupObj.owner = obj["owner_id"]
		groupObj.title = obj["title"]
		if checkAccessTokenValid(groupObj) is True:
			group = bll.createGroup(groupObj)
		data["group"] = bll.groupToDictConverter(group)
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def addGroupMembers():
	groupObj = Collection()
	response = {}
	data = {}
	obj = request.json
	try:
		groupObj.access_token = obj["access_token"]
		groupObj.id = obj["group_id"]
		groupObj.members = obj["members_id"]
		if checkAccessTokenValid(groupObj) is True:
			group = bll.addGroupMembers(groupObj)
		data["group"] = bll.groupToDictConverter(group)
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response



def remGroupMembers():
	groupObj = Collection()
	response = {}
	data = {}
	obj = request.json
	try:
		groupObj.access_token = obj["access_token"]
		groupObj.id = obj["group_id"]
		groupObj.members = obj["members_id"]
		if checkAccessTokenValid(groupObj) is True:
			group = bll.remGroupMembers(groupObj)
		data["group"] = bll.groupToDictConverter(group)
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response



def addNewTask():
	taskObj = Collection()
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
		taskObj.group_id = obj["group_id"]
		try:
			taskObj.collaborator_count = obj["collaborator_count"]
		except KeyError:
			taskObj.collaborator_count = 1
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
	taskObj = Collection()
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
		taskObj.collaborator_count = obj["collaborator_count"]
		taskObj["group_id"] = obj["group_id"]
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
	taskObj = Collection()
	response = {}
	data = {}
	obj = request.json
	# try:
	taskObj.access_token = obj["access_token"]
	taskObj.id = obj["id"]
	taskObj.collaborators = obj["collaborators"]
	taskObj.group_id = obj["group_id"]
	if checkAccessTokenValid(taskObj) is True:
		task = bll.addCollaborators(taskObj)
	response["status"] = RESPONSE_SUCCESS
	response["data"] = bll.taskToDictConverter(task)
	# except Exception as e:
	# 	response["status"] = RESPONSE_FAILED
	# 	response["message"] = str(e)
	# 	if hasattr(e, "code"):
	# 		response["code"] = e.code
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