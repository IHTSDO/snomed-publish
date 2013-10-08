//APP

  MyApp.register('controller:rule', MyApp.RuleController, {singleton: false});
  MyApp.register('controller:pages', MyApp.PagesController, {singleton: false});
  MyApp.register('controller:searchInput', MyApp.SearchInputController, {singleton: false});
  MyApp.register('controller:searchResults', MyApp.SearchResultsController, {singleton: false});

//MODELS  

  MyApp.RuleModel = Ember.Object.extend({
    id: 0,
    type: "",
    concepts: null,
    left: null,
    right: null,
    displayName: function(){
      return ' Rule ' + this.get('id') + ' ';
    }.property('id')
  });
  

// CONCEPTS
  MyApp.ConceptsController = Ember.ObjectController.extend({
    model: null,
    needs: ["index"]
  });

  MyApp.ConceptsController.reopenClass({
    getConcepts: function(pageIndex, pageSize){
      return Ember.Deferred.promise(function(p) {
        var startIndex = (pageIndex - 1) * pageSize;
        var query = "http://" + location.hostname + ":" + location.port + "/" + location.pathname + "/../concepts.json?start=" + startIndex + "&rows=" + pageSize;
        console.log('executing query: ' + query);
        p.resolve($.getJSON(query)
          .then(function(blah) {
              var concepts = Ember.A();
              blah.concepts.forEach(function (doc) {
                var concept = MyApp.Concept.create();
                concept.id = doc.id;
                concept.title = doc.title;
                concept.active = doc.active;
                concept.effectiveTime= doc.effectiveTime;
                concepts.pushObject(concept);
              });
              return concepts;
          }) //then
        );//resolve
      });//deferred promise
    },//getConcepts
  });//reopen

  MyApp.ConceptsRoute = Ember.Route.extend({
    model: function(){
      return MyApp.ConceptsController.getConcepts(-1, -1);
    }  
  })

  MyApp.ConceptsView = Ember.View.extend({
    templateName: 'concepts'
  });

// RULES
  MyApp.RulesController = Ember.ObjectController.extend({
    model: null,
    counter: -1,
    actions:{
      unionRule: function(){
        console.log('new union rule')
        var newRule = MyApp.RuleModel.create();
        newRule.set('type', 'UNION');
        newRule.set('id', this.get('counter'));
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.setRule').set('model', newRule);
        return false;
      },
      differenceRule: function(){
        console.log('new difference rule')
        var newRule = MyApp.RuleModel.create();
        newRule.set('type', 'DIFFERENCE');
        newRule.set('id', this.get('counter'));
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.setRule').set('model', newRule);
        return false;
      },
      intersectionRule: function(){
        console.log('new intersection rule')
        var newRule = MyApp.RuleModel.create();
        newRule.set('type', 'INTERSECTION');
        newRule.set('id', this.get('counter'));
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.setRule').set('model', newRule);
        return false;
      },
      symDifferenceRule: function(){
        console.log('new symmetric difference rule')
        var newRule = MyApp.RuleModel.create();
        newRule.set('type', 'SYMMETRIC');
        newRule.set('id', this.get('counter'));
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.setRule').set('model', newRule);
        return false;
      },
      listRule: function(){
        console.log('new list rule');
        var newRule = MyApp.RuleModel.create();
        newRule.set('type', 'LIST');
        newRule.set('id', this.get('counter'));
        var c1 = MyApp.Concept.create();
        var c2 = MyApp.Concept.create();
        var c3 = MyApp.Concept.create();
        c1.set('id', 321987003);
        c2.set('id', 441519008);
        c3.set('id', 128665000);
        newRule.set('concepts', [c1, c2, c3]);
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.listRule').set('model', newRule);
        return false;
      }     
    },  
    needs: ["index", "setRule", "listRule"]
  });

  MyApp.RulesController.reopenClass({
    getRules: function(pageIndex, pageSize){
      return Ember.Deferred.promise(function(p) {
        var startIndex = (pageIndex - 1) * pageSize;
        var currentUrl = "http://" + location.hostname + ":" + location.port + "/" + location.pathname;
        var readyUrl = currentUrl.substring(0, currentUrl.lastIndexOf('/'));
        var query = readyUrl + ".json?start=" + startIndex + "&rows=" + pageSize;
        console.log('executing refset query: ' + query);
        p.resolve($.getJSON(query)
          .then(function(blah) {
              console.log('blah is ' + blah);
              console.log('plan is ' + blah.plan);
              console.log('id is ' + blah.plan.id);
              console.log('rules are ' + blah.plan.refsetRules);
              console.log('refset rules are of class: ' + blah.plan.refsetRules.constructor.name);
              var rules = Ember.A(); 

              blah.plan.refsetRules.forEach(function (doc) {
                var rule = MyApp.RuleModel.create();
                rule.set('id', doc.id);
                rule.set('type', doc.type);
                rule.set('left', doc.left);
                rule.set('right', doc.right);
                var concepts = Ember.A();
                console.log('concepts class is ' + doc.concepts.constructor.name);
                doc.concepts.forEach(function(doc_c){
                  var concept = MyApp.Concept.create();
                  concept.set('id', doc_c.id);
                  concept.set('title', doc_c.title);
                  concepts.pushObject(concept);
                });
                rule.set('concepts', concepts);
                rules.pushObject(rule);
              });
              return rules;
          }) //then
        );//resolve
      });//deferred promise
    },//getConcepts
  });//reopen

  MyApp.RulesRoute = Ember.Route.extend({
    model: function(){
      console.log('In RulesRoute, loading Rules');
      return MyApp.RulesController.getRules(-1, -1);
    }  
  })

  MyApp.RulesView = Ember.View.extend({
    templateName: 'rules'
  });

// LIST RULE MODAL  
  MyApp.ListRuleController = Ember.ObjectController.extend({
    model: null,
    needs: "index",
    isSearching: false,
    actions:{
      gosearch: function(){
        this.set('isSearching', true);
        return false;
      },
      goconcepts: function(){
        this.set('isSearching', false);
        return false;
      },      
      save: function(){
        this.get('controllers.index.model').pushObject(this.get('model'));        
        return false;
      }
     }    
  });

  MyApp.ListRuleRoute = Ember.ObjectController.extend({
    renderTemplate: function(){
      console.log('I need to be here');
    }
  })

  MyApp.ListRuleView = Ember.View.extend({
      templateName: 'listRule',
      
      conceptsView: Ember.View.extend({
        templateName: 'listRuleConcepts'
      }),
      searchView: Ember.View.extend({
        templateName: 'listRuleSearch'
      })
  });


//SET RULE MODALS
  MyApp.SetRuleController = Ember.ObjectController.extend({
    model: null,
    appName: 'Set Rule',
    needs: "rules",

    actions:{
      saverule: function(){
        this.set('model.left', this.get('model.left.id'));
        this.set('model.right', this.get('model.right.id'));
        this.get('controllers.rules.model').pushObject(this.get('model'));
        return false;
      }
     }    
  });

  MyApp.SetRuleView = Ember.View.extend({
      templateName: 'setRule'
  });


// DISPLAY RULE
  MyApp.RuleController = Ember.ObjectController.extend({
    model: null,
    isList: function(){
      return (this.get('model.type') === "LIST");
    }.property('type'),
    isUnion: function(){
      return (this.get('model.type') === "UNION");
    }.property('type'),
    isDifference: function(){
      return (this.get('model.type') === "DIFFERENCE");
    }.property('type'),
    isIntersection: function(){
      return (this.get('model.type') === "INTERSECTION");
    }.property('type'),
    isSymDifference: function(){
      return (this.get('model.type') === "SYMMETRIC");
    }.property('type')
  });
  
  MyApp.RuleView = Ember.View.extend({
    templateName: 'rule',
  });  

