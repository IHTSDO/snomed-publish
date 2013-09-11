// APPLICATION
// -----------

App = Ember.Application.create();

App.ApplicationController = Ember.Controller.extend({
  appName: 'Snomed Search'
});

App.Router.map(function() {
  this.route("index", {path: "/"});
});


