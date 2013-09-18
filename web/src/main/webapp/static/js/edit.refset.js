// APPLICATION
// -----------

TextSearch.IndexRoute = Ember.Route.extend({
  setupController: function(controller, model) {
    console.log('In pagesRoute.setupController');
    this.controllerFor('pages').set('maxPageIndexesShown', 5);
  },
  actions:{
    click: function(concept){
      $("#concept-title").text(concept.title);
      $("#concept-title").attr('href', 'http://browser.sparklingideas.co.uk/ontology/1/concept/' + concept.id);
      $("#concept-id").val(concept.id);
      var selected = $(".toggle-find-concept.selected");
      var notSelected = $(".toggle-find-concept.not-selected");
      selected.attr('class', 'toggle-find-concept selected active');
      notSelected.attr('class', 'toggle-find-concept not-selected inactive');
      $('#textSearchModal').modal('hide');
    }
  }
});