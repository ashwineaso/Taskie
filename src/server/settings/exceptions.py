class TaskWithIDNotFound(Exception):
	"""docstring for TaskWithIDNotFound"""
	def __init__(self, arg):
		super(TaskWithIDNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)


class UserNotFound(Exception):
	"""docstring for UserNotFound"""
	def __init__(self, arg):
		super(UserNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)
		

class PasswordMismatch(Exception):
	"""docstring for PasswordMismatch"""
	def __init__(self, arg):
		super(PasswordMismatch, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)
		

class UserAlreadyExists(Exception):
	"""docstring for UserAlreadyExists"""
	def __init__(self):
		self.message = "User already exists"
		self.code = 1002 

	def __str__(self):
		return repr(self.arg)


class TokenNotFound(Exception):
	"""docstring for TokenNotFound"""
	def __init__(self, arg):
		super(TokenNotFound, self).__init__()
		self.arg = arg

	def __str__(self):
		return repr(self.arg)