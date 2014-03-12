// APPLICATION
// -----------

MyApp = Ember.Application.create({
  rootElement: '#search',
  LOG_TRANSITIONS: true
});

MyApp.Router.map(function() {
  this.route('index', {path: '/'});
});

MyApp.ApplicationRoute = Ember.Route.extend({
  setupController: function(controller, model) {
    this.controllerFor('textSearch').set('maxPageIndexesShown', 10);
  },
  actions:{
    click: function(concept){
      window.location.assign("/version/1/concept/" + concept.id);
    }
  },
}); 

MyApp.TextSearchRoute = Ember.Route.extend({
  setupController: function(controller, model){
    $('#tandcModal').modal('show');
  }
});



$(window).load(function(){
    $('#tandcModal').modal('show');
});
