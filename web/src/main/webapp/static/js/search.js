// APPLICATION
// -----------

TextSearch.IndexRoute = Ember.Route.extend({
  setupController: function(controller, model) {
    console.log('In pagesRoute.setupController');
    this.controllerFor('pages').set('maxPageIndexesShown', 10);
  },
  actions:{
    click: function(concept){
      window.location.assign("http://browser.sparklingideas.co.uk/ontology/1/concept/" + concept.id);
    }
  }
});