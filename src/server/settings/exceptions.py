class TaskWithIDNotFound(object):
	"""docstring for TaskWithIDNotFound"""
	def __init__(self, arg):
		super(TaskWithIDNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)
		
# class TaskWithIDNotFound(Exception):
# 	def __init__(self):
# 		super(TaskWithIDNotFound, self).__init__()
# 		self.message = "Cannot find the task with the ID"
# 		self .code = 2001

class UserNotFound(object):
	"""docstring for UserNotFound"""
	def __init__(self, arg):
		super(UserNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)
		

# class  UserNotFound(Exception):
# 	def __init__(self):
# 		super(UserNotFound, self).__init__()
# 		self.message = "User not found in database"
# 		self.code = 1001


# class UserAlreadyExists(Exception):
# 	"""docstring for ClassName"""
# 	def __init__(self):
# 		super(UserAlreadyExists, self).__init__()
# 		self.message = "User already exists in Database"
# 		self.code = 1002

class PasswordMismatch(object):
	"""docstring for PasswordMismatch"""
	def __init__(self, arg):
		super(PasswordMismatch, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)
		
# class PasswordMismatch(Exception):
# 	"""docstring for ClassName"""
# 	def __init__(self):
# 		super(PasswordMismatch, self).__init__()
# 		self.message = "Username and Password does not match"
# 		self.code = 1003

class UserAlreadyExists(object):
	"""docstring for UserAlreadyExists"""
	def __init__(self, arg):
		super(UserAlreadyExists, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)


class TokenNotFound(object):
	"""docstring for TokenNotFound"""
	def __init__(self, arg):
		super(TokenNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)