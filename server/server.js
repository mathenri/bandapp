// server.js

// import packages
var express = require('express'),
	app = express(),
	bodyParser = require('body-parser'),
	mongoose = require('mongoose'),
	winston = require('winston'),
	expressWinston = require('express-winston')
	path = require('path');

// setup body parser
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

// serve files from "public" directory
app.use(express.static(path.join(__dirname, 'public')));

// setup database connection
mongoose.connect('mongodb://localhost/band')

// database model
var Event = require('./app/models/event');
var Song = require('./app/models/song')

var port = process.env.PORT || 8080;

var router = express.Router();

// this applies to all requests ('middleware')
router.use(function(req, res, next) {
	//TODO: Authentication?

	// go to next routes, don't stop here..
	next();
});

// test route to ensure everything is working
router.get('/', function (req, res) {
	res.json({ message: 'Welcome to BandApp API!'})
});

router.route('/songs')

	// create song database entry
	.post(function(req, res) {

		// create instance of the event model
		var song = new Song();
		song.songName = req.body.songName;
		song.part = req.body.part;
		song.fileName = req.body.fileName;

		song.save(function(err) {
			if (err) 
				res.send(err);

			res.json({ message: 'Song created!' });
		});
	})

	// get all songs
	.get(function(req, res) {
		Song.find(function(err, songs) {
			if (err)
				res.send(err);

			res.json(songs)
		});
	});

router.route('/events')

	// create event
	.post(function(req, res) {

		// create instance of the event model
		var event = new Event();
		event.type = req.body.type;
		event.location = req.body.location;
		event.date = req.body.date;
		event.foodResponsible = req.body.foodResponsible;
		event.absent = req.body.absent;

		event.save(function(err) {
			if (err) 
				res.send(err);

			res.json({ message: 'Event created!' });
		});

	})

	// get all events
	.get(function(req, res) {
		Event.find(function(err, events) {
			if (err)
				res.send(err);

			res.json(events)
		});
	});

router.route('/events/:event_id')

	// get event with a given id
	.get(function(req, res) {
		Event.findById(req.params.event_id, function(err, event) {
			if (err)
				res.send(err)
			res.send(event);
		});
	})

	// update event with given id
	.put(function(req, res) {
		Event.findById(req.params.event_id, function(err, event) {
			if (err)
				res.send(err);
			event.type = req.body.type;
			event.location = req.body.location;
			event.date = req.body.date;
			event.foodResponsible = req.body.foodResponsible;
			event.absent = req.body.absent;

			event.save(function(err) {
				if (err)
					res.send(err);
				res.json({message: 'Event updated!'});
			});
		});
	})

	// delete an event with a given id
	.delete(function(req, res) {
		Event.remove({
			_id: req.params.event_id
		}, function(err, event) {
			if (err)
				res.send(err);
			res.json({ message: 'Successfully deleted'});
		});
	});

// logging middleware
app.use(expressWinston.logger({
	transports: [
		new winston.transports.Console({
			json: false,
			colorize: true,
			timestamp: true
		})
	],
	meta: false, // log the meta data about the request
	expressFormat: true, // default message format
	colorize: true, // colors on message
}));

// prefix all routes with /api
app.use('/api', router);

// start server
app.listen(port);
console.log('Server listening on port ' + port);