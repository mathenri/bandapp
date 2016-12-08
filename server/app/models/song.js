// app/models/event.js

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventSchema = new Schema({
	songName: String,
	part: String,
	fileName: String
});

module.exports = mongoose.model('Song', EventSchema);