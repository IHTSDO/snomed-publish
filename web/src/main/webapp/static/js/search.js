// APPLICATION
// -----------

TextSearch.ApplicationRoute = Ember.Route.extend({
  setupController: function(controller, model) {
    this.controllerFor('pages').set('maxPageIndexesShown', 10);
  },
  actions:{
    click: function(concept){
      window.location.assign("http://browser.snomedtools.com/version/1/concept/" + concept.id);
    }
  }
});