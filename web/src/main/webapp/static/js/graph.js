// APPLICATION
// -----------

MyApp = Ember.Application.create({
  rootElement: '#graph',
  LOG_TRANSITIONS: true,
  LOG_BINDINGS: true  
});

//MyApp.register('controller:textSearchModal', MyApp.TextSearchModalController, {singleton: false});

MyApp.Router.map(function() {
  this.route('index', {path: '/'});
});

MyApp.ApplicationRoute = Ember.Route.extend({
  setupController: function(controller, model) {
    graphController = this.controllerFor('GraphSearch')
    console.log('setting up ' + graphController);
    graph = MyApp.Graph.create();
    graph.set('filters', Ember.A());
    graph.set('triples', Ember.A());
    graphController.set('model', graph);
    console.log('setting up textsearch');
    //this.controllerFor('textSearch').set('maxPageIndexesShown', 5);
  },
  actions:{
    click: function(concept){
      console.log('nothing handled click event!')
      //window.location.assign("http://browser.snomedtools.com/version/1/concept/" + concept.id);
    }
  },
});


MyApp.GraphSearchController = Ember.ObjectController.extend({
  model: Ember.A(),
  variables: Ember.A(),
  results: null;
  counter: -1,
  sparql: function(){
    console.log('In sparql generator');
    var generated =
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
      "PREFIX c: <http://snomedtools.info/snomed/version/1/concept/rdfs/>\n" +
      "PREFIX d: <http://snomedtools.info/snomed/version/1/description/rdfs/>\n" +
      "PREFIX s: <http://snomedtools.info/snomed/version/1/statement/rdfs/>\n" +
      "PREFIX sn: <http://snomedtools.info/snomed/term/>\n" +
      "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\n" +
      "SELECT * WHERE {\n";
    this.get('model').forEach(function(triple, tindex, enumerable){
      generated = generated + triple.get('sparql') + '.\n';
    });
    generated = generated + "} LIMIT 50";
    console.log('returning generated sparql: ' + generated);
    return generated;
  }.property('model.@each.sparql'),
  actions: {
    newtriple: function(){
      this.set('controllers.tripleEditor.isEdit', false);
      console.log('handling new triple action. Setting empty triple');
      triple = MyApp.Triple.create();
      triple.id = this.get('counter');
      this.set('counter', triple.id + 1);
      triple.set('subject', MyApp.Subject.create());
      triple.set('predicate', MyApp.Predicate.create());
      triple.set('object', MyApp.Object.create());
      console.log('Setting triple ' + JSON.stringify(triple) + ', on controller ' + this.get('controllers.tripleEditor'));
      $('#subject-type li.variable a').tab('show');
      this.get('controllers.tripleEditor').set('model', triple);
    },
    savetriple: function(triple){
      console.log('handling save triple action with triple ' + JSON.stringify(triple));
      var triples = this.get('model');
      var found = triples.findBy('id', triple.id);
      if (found === undefined){
        console.log('found is null, pushing object');
        triples.pushObject(triple);
      }else{
        console.log('found is not null, replacing');
        console.log('indexOf is ' + triples.indexOf(found));
      }
    },
    edittriple: function(triple){
      this.set('controllers.tripleEditor.isEdit', true);
      console.log('handling edit triple action with triple ' + JSON.stringify(triple));
      $('#subject-type li.variable a').tab('show');
      this.get('controllers.tripleEditor').set('model', triple);
    }
  },
  needs: ['tripleEditor'],

});

MyApp.GraphSearchRoute = Ember.Route.extend({
});

MyApp.GraphSearchView = Ember.View.extend({
  templateName: "graphSearch",
  blah: function (){
    console.log('in blah, model is :' + JSON.stringify(this.get('model')));
    return blah;
  }.property('id'),
  isEdit: function(){
    console.log('in isEdit, value is ' + this.get('isEdit'));
    return this,get('isEdit');
  }.property('isEdit')
});

MyApp.TripleEditorController = Ember.ObjectController.extend({
  model: undefined,
  needs: "graphSearch",
  isEdit: false,
  actions:{
    subjectType: function(subjectType){
      //TODO: CLEAN THIS UP WITH A {{bind-attr}} INSTEAD
      console.log('handling event subjectType with type ' + subjectType);
      console.log('model is ' + JSON.stringify(this.get('model')));
      this.set('subject.type', subjectType);

      $('#subject-type li.variable')[0].setAttribute('class', 'variable');
      $('#subject-type li.snomed')[0].setAttribute('class', 'snomed');
      
      if (subjectType === 'variable'){
        $('#subject-type li.variable')[0].setAttribute('class', 'active variable');
      } else {
        $('#subject-type li.snomed')[0].setAttribute('class', 'active snomed');
      }
      return false;
    },
    subjectComponentType: function(subjectComponentType){
      console.log('handling event subjectComponentType with type ' + subjectComponentType);
      this.set('subject.componentType', subjectComponentType);
      return false;
    },
    predicateType: function(predicateType){
      console.log('handling event predicateType with type ' + predicateType);
      this.set('predicate.type', predicateType);
      return false;
    },
    predicateStructural: function(predicateStructural){
      console.log('handling event predicateStructural with type ' + predicateStructural);
      this.set('predicate.structural', predicateStructural);
      return false;
    },
    predicatePrimitiveType: function(primitiveType){
      console.log('handling event predicatePrimitiveType with type ' + primitiveType);
      this.set('predicate.primitiveType', primitiveType);
      return false;
    },
    predicatePrimitiveCategory: function(predicatePrimitiveCategory){
      console.log('handling event predicatePrimitiveCategory with type ' + predicatePrimitiveCategory);
      this.set('predicate.primitiveCategory', predicatePrimitiveCategory);
      return false;
    },
    objectType: function(objectType){
      console.log('handling event objectType with type ' + objectType);
      this.set('object.type', objectType);
      return false;
    },
    objectComponentType: function(objectComponentType){
      console.log('handling event objectComponentType with type ' + objectComponentType);
      this.set('object.componentType', objectComponentType);
      return false;
    },    
  },

  // SUBJECT


  subjectComponentTypeConceptClass: function(){
    //console.log('in subjectComponentTypeConceptClass');
    var returned = 'concept';
    if (this.get('subject.componentType') === 'concept'){
      returned = returned + ' active';
    }
    return returned;
  }.property('subject.componentType'),
  subjectComponentTypeDescriptionClass: function(){
    //console.log('in subjectComponentTypeDescriptionClass');
    var returned = 'description';
    if (this.get('subject.componentType') === 'description'){
      returned = returned + ' active';
    }
    return returned;
  }.property('subject.componentType'),  


  // PREDICATE
  
  predicateTypeVariableClass: function(){
    //console.log('in predicateTypeVariableClass');
    var returned = 'variable';
    if (this.get('predicate.type') === 'variable'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.type'),
  predicateTypeStructuralClass: function(){
    //console.log('in predicateTypeStructuralClass');
    var returned = 'structural';
    if (this.get('predicate.type') === 'structural'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.type'),
  predicateTypeConceptClass: function(){
    //console.log('in predicateTypeConceptClass');
    var returned = 'concept';
    if (this.get('predicate.type') === 'concept'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.type'),
  predicateTypePrimitiveClass: function(){
    //console.log('in predicateTypePrimitiveClass');
    var returned = 'primitive';
    if (this.get('predicate.type') === 'primitive'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.type'),

  predicatePrimitiveDescriptionClass: function(){
    //console.log('in predicatePrimitiveDescriptionClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'description'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveStatusClass: function(){
    //console.log('in predicatePrimitiveStatusClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'status'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveTypeClass: function(){
    //console.log('in predicatePrimitiveTypeClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'type'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),  
  predicatePrimitiveModuleClass: function(){
    //console.log('in predicatePrimitiveModuleClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'module'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveActiveClass: function(){
    //console.log('in predicatePrimitiveActiveClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'active'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveEffectiveTimeClass: function(){
    //console.log('in predicatePrimitiveEffectiveTimeClass');
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'effectiveTime'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveModifierClass: function(){
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'modifier'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveCharacteristicTypeClass: function(){
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'characteristicType'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveGroupClass: function(){
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'group'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),
  predicatePrimitiveCaseSignificanceClass: function(){
    var returned = 'btn btn-default';
    if (this.get('predicate.primitiveType') === 'caseSignificance'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveType'),

  predicateTypePrimitiveConceptClass: function(){
    var returned = 'concept';
    if (this.get('predicate.primitiveCategory') === 'concept'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveCategory'),
  predicateTypePrimitiveDescriptionClass: function(){
    var returned = 'description';
    if (this.get('predicate.primitiveCategory') === 'description'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveCategory'),
  predicateTypePrimitiveTripleClass: function(){
    var returned = 'triple';
    if (this.get('predicate.primitiveCategory') === 'triple'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.primitiveCategory'),

  predicateStructuralTypeClass: function(){
    var returned = 'btn btn-default type';
    if (this.get('predicate.structural') === 'type'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.structural'),
  predicateStructuralSubjectClass: function(){
    var returned = 'btn btn-default type';
    if (this.get('predicate.structural') === 'subject'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.structural'),
  predicateStructuralPredicateClass: function(){
    var returned = 'btn btn-default type';
    if (this.get('predicate.structural') === 'predicate'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.structural'),
  predicateStructuralObjectClass: function(){
    var returned = 'btn btn-default type';
    if (this.get('predicate.structural') === 'object'){
      returned = returned + ' active';
    }
    return returned;
  }.property('predicate.structural'),

 

  objectComponentTypeConceptClass: function(){
    //console.log('in objectComponentTypeConceptClass');
    var returned = 'concept';
    if (this.get('object.componentType') === 'concept'){
      returned = returned + ' active';
    }
    return returned;
  }.property('object.componentType'),
  objectComponentTypeDescriptionClass: function(){
    //console.log('in objectComponentTypeDescriptionClass');
    var returned = 'description';
    if (this.get('object.componentType') === 'description'){
      returned = returned + ' active';
    }
    return returned;
  }.property('object.componentType'),    

});

MyApp.TripleEditorView = Ember.View.extend({

});

MyApp.TextSearchSubjectController = Ember.ObjectController.extend({
  templateName: 'textSearchSubject',
  actions:{
    click: function(concept){
      console.log('In TextSearchSubjectController: Handling click for concept ' + JSON.stringify(concept));
      $('#textSearchModalSubject').modal('hide');
      this.get('controllers.tripleEditor').set('subject.concept', concept);
      return false;
    } 
  },
  needs: "tripleEditor"
});

MyApp.TextSearchPredicateController = Ember.ObjectController.extend({
  templateName: 'textSearchPredicate',
  actions:{
    click: function(concept){
      console.log('In TextSearchPredicateController: Handling click for concept ' + JSON.stringify(concept));
      $('#textSearchModalPredicate').modal('hide');
      this.get('controllers.tripleEditor').set('predicate.concept', concept);
      return false;
    } 
  },
  needs: "tripleEditor"
});

MyApp.TextSearchObjectController = Ember.ObjectController.extend({
  templateName: 'textSearchObject',
  actions:{
    click: function(concept){
      console.log('In TextSearchObjectController: Handling click for concept ' + JSON.stringify(concept));
      $('#textSearchModalObject').modal('hide');
      this.get('controllers.tripleEditor').set('object.concept', concept);
      return false;
    } 
  },
  needs: "tripleEditor"
});

MyApp.Graph = Ember.Object.extend({
  triples: undefined,
  filters: undefined
}) 

MyApp.SingleResult = Ember.Object.extend({
  concept: null,
  description: null,
  statement: null,
  value: null,
  url: function(){
    if (this.get('isConcept')){
      return "http://browser.snomedtools.com/version/1/concept/" + this.get('concept.id');
    }
    if (this.get('isDescription')){
      return "http://browser.snomedtools.com/version/1/description/" + this.get('description.id');
    }
    if (this.get('isStatement')){
      return "http://browser.snomedtools.com/version/1/statement/" + this.get('statement.id');
    }else{
      return "error!";
    }
  },

  setContent: function (content){
    if (content.contains('/concept/')){
      var concept = MyApp.Concept.create(),
          id,
          title;
      concept.set('id', id);
      concept.set('title', title);
      this.set('concept', content);
    }
    else if (content.contains('/description/')){
      this.set('concept', content);
    }
    else if (content.contains('/statement/')){
      this.set('concept', content);
    } 
    else {
      this.set('value', content);
    }
  }

  isConcept: function(){
    return (this.get('concept') != null);
  }.property('concept'),
  isDescription: function(){
    return (this.get('description') != null);
  }.property('description'),
  isStatement: function(){
    return (this.get('statement') != null);
  }.property('statement'),
  isValue: function(){
    return (this.get('value') != null);
  }.property('value')
})

MyApp.ResultSet = Ember.Object.extend({
  singleResult: Ember.A()
});

MyApp.Filter = Ember.Object.extend({
  expression: undefined
});

MyApp.Subject = Ember.Object.extend({
  type: 'variable',
  variable: null,
  componentType: 'concept',
  concept: null,
  description: null,
  sparql: function(){
    console.log('in subject sparql');
    var type = this.get('type');
    if (type === 'variable'){
      return '$' + this.get('variable');
    }
    if (type === 'snomed'){
      var componentType = this.get('componentType');
      if (componentType === 'concept'){
        return 'c:' + this.get('concept.id');
      }
      if (componentType === 'description'){
        return 'd:' + this.get('description.id');
      }
    }
  }.property('type', 'variable', 'componentType', 'concept', 'description')  
});

MyApp.Predicate = Ember.Object.extend({
  type: 'variable',
  variable: null,
  concept: null,
  structural: null,
  primitiveType: null,
  primitiveCategory: 'concept',
  sparql: function(){
    console.log('in predicate sparql');
    var type = this.get('type');
    if (type === 'variable'){
      return '$' + this.get('variable');
    }
    if (type === 'concept'){
      return 'c:' + this.get('concept.id');
    }
    if (type === 'primitive'){
      return 'sn:' + this.get('primitiveType');
    }
    if (type === 'structural'){
      return 'rdf:' + this.get('structural');
    } 
    return "error! (type = " + type + ")";
  }.property('type', 'variable', 'concept', 'structural', 'primitiveType')
});

MyApp.Object = Ember.Object.extend({
  type: 'variable',
  variable: null,
  componentType: 'concept',
  concept: null,
  description: null,
  sparql: function(){
    console.log('in object sparql');
    var type = this.get('type');
    if (type === 'variable'){
      return '$' + this.get('variable');
    }
    if (type === 'snomed'){
      var componentType = this.get('componentType');
      if (componentType === 'concept'){
        return 'c:' + this.get('concept.id');
      }
      if (componentType === 'description'){
        return 'd:' + this.get('description.id');
      }
    }
  }.property('type', 'variable', 'componentType', 'concept', 'description') 
});

MyApp.Triple = Ember.Object.extend({
  id: null,
  subject: null,
  predicate: null,
  object: null,

  sparql: function(){
    console.log('in triple sparql generator');
    var returned = this.get('subject.sparql') + ' ' + this.get('predicate.sparql') + ' ' + this.get('object.sparql')
    console.log('generated sparql ' + returned);
    return returned;
  }.property('subject.sparql', 'predicate.sparql', 'object.sparql'),

  // SUBJECT
  isSubjectVariable: function(){
    var returned = (this.get('subject.type') === 'variable');
    console.log('isSubjectVariable returns ' + returned + ' and subjectType is ' + this.get('subject.type'));
    return returned;
  }.property('subject.type'),
  isSubjectSnomed: function(){
    var returned = (this.get('subject.type') === 'snomed');
    //console.log('isSubjectSnomed returns ' + returned + ' and subjectType is ' + this.get('subject.type'));
    return returned;
  }.property('subject.type'), 
  isSubjectComponentTypeConcept: function(){
    var returned = (this.get('subject.componentType') === 'concept');
    //console.log('isSubjectComponentTypeConcept returns ' + returned);
    return returned;
  }.property('subject.componentType'),
  isSubjectComponentTypeDescription: function(){
    var returned = (this.get('subject.componentType') === 'description');
    //console.log('isSubjectComponentTypeDescription returns ' + returned);
    return returned;
  }.property('subject.componentType'),

  // PREDICATE
  isPredicateVariable: function(){
    var returned = (this.get('predicate.type') === 'variable');
    //console.log('isPredicateVariable returns ' + returned + ' and predicate.type is ' + this.get('predicate.type'));
    return returned;
  }.property('predicate.type'),
  isPredicateStructural: function(){
    var returned = (this.get('predicate.type') === 'structural');
    //console.log('isPredicateStructural returns ' + returned + ' and predicate.type is ' + this.get('predicate.type'));
    return returned;
  }.property('predicate.type'),
  isPredicateConcept: function(){
    var returned = (this.get('predicate.type') === 'concept');
    //console.log('isPredicateConcept returns ' + returned + ' and predicate.type is ' + this.get('predicate.type'));
    return returned;
  }.property('predicate.type'),
  isPredicatePrimitive: function(){
    var returned = (this.get('predicate.type') === 'primitive');
    //console.log('isPredicatePrimitive returns ' + returned + ' and predicate.type is ' + this.get('predicate.type'));
    return returned;
  }.property('predicate.type'),
  isPredicatePrimitiveCategoryConcept: function(){
    var returned = (this.get('predicate.primitiveCategory') === 'concept');
    //console.log('isPredicatePrimitiveCategoryConcept returns ' + returned + ' and predicate.primitiveCategoty is ' + this.get('predicate.primitiveCategory'));
    return returned;
  }.property('predicate.primitiveCategory'),
  isPredicatePrimitiveCategoryDescription: function(){
    var returned = (this.get('predicate.primitiveCategory') === 'description');
    //console.log('isPredicatePrimitiveCategoryDescription returns ' + returned + ' and predicate.primitiveCategoty is ' + this.get('predicate.primitiveCategory'));
    return returned;
  }.property('predicate.primitiveCategory'),  
  isPredicatePrimitiveCategoryTriple: function(){
    var returned = (this.get('predicate.primitiveCategory') === 'triple');
    //console.log('isPredicatePrimitiveCategoryTriple returns ' + returned + ' and predicate.primitiveCategoty is ' + this.get('predicate.primitiveCategory'));
    return returned;
  }.property('predicate.primitiveCategory'),

  // OBJECT
  isObjectVariable: function(){
    var returned = (this.get('object.type') === 'variable');
    //console.log('isObjectVariable returns ' + returned + ' and objectType is ' + this.get('object.type'));
    return returned;
  }.property('object.type'),
  isObjectSnomed: function(){
    var returned = (this.get('object.type') === 'snomed');
    //console.log('isObjectSnomed returns ' + returned + ' and objectType is ' + this.get('object.type'));
    return returned;
  }.property('object.type'), 
  isObjectComponentTypeConcept: function(){
    var returned = (this.get('object.componentType') === 'concept');
    //console.log('isObjectComponentTypeConcept returns ' + returned);
    return returned;
  }.property('object.componentType'),
  isObjectComponentTypeDescription: function(){
    var returned = (this.get('object.componentType') === 'description');
    //console.log('isObjectComponentTypeDescription returns ' + returned);
    return returned;
  }.property('object.componentType'),    
});



MyApp.Resource = Ember.Object.extend({
  id: undefined,
  title: undefined
})

MyApp.Variable = MyApp.Resource.extend({
  name: undefined,
  isVariable: true
})

