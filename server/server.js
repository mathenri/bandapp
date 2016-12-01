// server.js

// import packages
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');

// setup body parser
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

// setup database connection
mongoose.connect('mongodb://localhost/band')

var port = process.env.PORT || 8080;

var Event = require('./app/models/event');

var router = express.Router();

// this applies to all requests ('middleware')
router.use(function(req, res, next) {
	console.log('Request received.');
	//TODO: Authentication?

	// go to next routes, don't stop here..
	next();
});

// test route to ensure everything is working
router.get('/', function (req, res) {
	res.json({ message: 'hurra! welcome to the api!'})
});

router.route('/events')

	// create event
	.post(function(req, res) {

		// create instance of the event model
		var event = new Event();
		event.type = req.body.type;
		event.location = req.body.location;
		event.date = req.body.date;

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

// prefix all routes with /api
app.use('/api', router);

// start server
app.listen(port);
console.log('Server listening on port ' + port);