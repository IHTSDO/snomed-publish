MyApp = Ember.Application.create({
	rootElement: '#refset',
	LOG_TRANSITIONS: true
});

MyApp.Router.map(function() {
	this.route('index', {path: '/'});
});