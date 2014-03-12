
// HELPERS

Handlebars.registerHelper('conceptUrl', function(options) {
  var concept = options.data.view.content;

  console.log('in concept url helper, concept is: ' + JSON.stringify(concept));
  
  return '/version/1/concept/' + concept.get('id');

  return bind;
});

MyApp.toEmberObject = function(plainObject) {
    // Return undefined or null
    if (!plainObject) {
        return plainObject;
    }
    // Build plain JS object with Ember Objects/Arrays/primitives
    var data = {};
    for (var key in plainObject) {
        var value = plainObject[key];  
        var type = Ember.typeOf(value);
        if (type === 'array') {
            var emberArray = Ember.A();
            //{
            //  replace: function (idx, amt, objects){
            //    console.log('IN REPLACE!!!')
            //    this.enumerableContentDidChange()
            //  }
            //});
            for (var i = 0; i < value.length; ++i) {
                emberArray.pushObject(MyApp.toEmberObject(value[i]));
            }
            data[key] = emberArray;
        } else if( type === 'object' ) {
            data[ key ] = MyApp.toEmberObject( value );
        } else if( type === 'string' || type === 'number' || type === 'boolean') {
            data[ key ] = value;
        }
    }
    var result = Ember.Object.create( data );
    return result;
};

MyApp.ConceptsController = Ember.ObjectController.extend({
  model: undefined,
  url: function(concept){
    console.log('in url. concept is ' + JSON.stringify(concept));
  }.property()
});

MyApp.ConceptsController.reopenClass({
  jsonServiceUrl: undefined,
  xmlServiceUrl: undefined,
  needs: "index",
  blah: function() {
    console.log("BLAH!");
    return "blah";
  }.property(),
  getConcepts: function(pageIndex, pageSize){
    return Ember.Deferred.promise(function(p) {
      var startIndex = (pageIndex - 1) * pageSize;
      var query = MyApp.ConceptsController.jsonServiceUrl + "?start=" + startIndex + "&rows=" + pageSize;
      console.log('executing query: ' + query);
      p.resolve($.getJSON(query)
        .then(function(refset) {
            var emberObject = MyApp.toEmberObject(refset);
            return emberObject;
        }) //then
      );//resolve
    });//deferred promise
  },//getConcepts
});//reopen

MyApp.ConceptsRoute = Ember.Route.extend({
  model: function(){
    console.log('Initialising service url');
    var currentUrl = "http://" + location.hostname + ":" + location.port + "/" + location.pathname;
    var baseUrl = currentUrl.substring(0, currentUrl.lastIndexOf('/'));
    var conceptUrl = location.pathname.substring(location.pathname.lastIndexOf('/'), location.pathname.length) + '/concepts.';
    var serviceUrl = baseUrl + conceptUrl;
    console.log('Found service url ' + serviceUrl);
    MyApp.ConceptsController.jsonServiceUrl = serviceUrl + 'json';
    MyApp.ConceptsController.xmlServiceUrl = serviceUrl + 'xml';
    return MyApp.ConceptsController.getConcepts(-1, -1);
  }
})

MyApp.ConceptsView = Ember.View.extend({
  templateName: 'concepts',

});
