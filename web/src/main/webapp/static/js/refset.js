
MyApp = Ember.Application.create({
  rootElement: '#refset',
  LOG_TRANSITIONS: true,
  LOG_BINDINGS: true
});

MyApp.Router.map(function() {
  this.route('index', {path: '/'});
  this.resource("concepts", {path: "/"});
});

MyApp.ApplicationRoute = Ember.Route.extend({
  actions: {
    click: function(concept){
      window.open('/version/1/concept/' + concept.get('id'));
      return false;
    },
  },
}); 

MyApp.IndexController = Ember.ObjectController.extend({

});

