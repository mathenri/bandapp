// app/models/event.js

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventSchema = new Schema({
	type: String,
	location: String,
	date: String
});

module.exports = mongoose.model('Event', EventSchema);