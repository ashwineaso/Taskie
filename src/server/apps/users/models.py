from mongoengine import *
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper

connect()


class User(Document):
	email = EmailField()
	name = StringField()

	def to_dict(self):
		return mongo_to_dict_helper(self)


__author__ = ["ashwineaso"]