//APP

  MyApp.register('controller:rule', MyApp.RuleController, {singleton: false});
  MyApp.register('controller:pages', MyApp.PagesController, {singleton: false});
  MyApp.register('controller:searchInput', MyApp.SearchInputController, {singleton: false});
  MyApp.register('controller:searchResults', MyApp.SearchResultsController, {singleton: false});

// HELPERS

Handlebars.registerHelper('bindings', function(options) {
  var rindex = options.data.view.contentIndex,
      rule = options.data.view.content;

  console.log('in bindings helper, rule is: [' + rule.get('id') + '], index is: ' + rindex);
  
  var bind = 
    "<input type=\"hidden\" ${attributes}=\"\" value=\"" + rule.get('id') + "\" name=\"plan.refsetRules[" + rindex + "].id\" id=\"plan.refsetRules[" + rindex + "].id\">\n" +
    "<input type=\"hidden\" ${attributes}=\"\" value=\"" + rule.get('type') + "\" name=\"plan.refsetRules[" + rindex + "].type\" type=\"plan.refsetRules[" + rindex + "].type\">\n" +
    "<input type=\"hidden\" ${attributes}=\"\" value=\"" + rule.get('left') + "\" name=\"plan.refsetRules[" + rindex + "].left\" left=\"plan.refsetRules[" + rindex + "].left\">\n" +
    "<input type=\"hidden\" ${attributes}=\"\" value=\"" + rule.get('right') + "\" name=\"plan.refsetRules[" + rindex + "].right\" right=\"plan.refsetRules[" + rindex + "].right\">\n";
  
  if (!rule.get('concepts')){
    rule.get('concepts').forEach(function(concept, cindex, enumerable){
      bind = bind + "<input id=\"plan.refsetRules[" + rindex + "].concepts[" + cindex + "].id\" type=\"hidden\" ${attributes}=\"\" value=\"" + concept.get('id') + "\" name=\"plan.refsetRules[" + rindex + "].concepts[" + cindex + "].id\">\n";
    });
  }
  return bind;
});


//MODELS  

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

  MyApp.RuleModel = Ember.Object.extend({
    id: undefined,
    type: undefined,
    concepts: undefined,
    left: undefined,
    right: undefined,
    displayName: function(){
      return ' Rule ' + this.get('id') + ' ';
    }.property('id')
  });
  


  MyApp.EditDetailsView = Ember.View.extend({
    templateName: 'editDetails'
  });

  MyApp.ViewDetailsView = Ember.View.extend({
    templateName: 'viewDetails'
  }); 

// RULES
  MyApp.RulesController = Ember.ObjectController.extend({
    model: undefined,
    //change model to content to apply ember sorting
    sortProperties: ['id'],
    isEdit: false,
    sortAscending: true,
    xmlUrl: function(){
      return '../' + this.get('publicId') + '.xml';
    }.property('publicId'),
    jsonUrl: function(){
      return '../' + this.get('publicId') + '.json';
    }.property('publicId'),
    conceptUrl: function(){
      return '/version/1/concept/' + this.get('concept');
    }.property(),
    rules: function (){
      console.log('in rules');
      return this.get('model.plan.refsetRules');
    }.property('model'),
    counter: -1,
    actions:{
      editDetails: function(){
        this.set('isEdit', true);
      },
      viewDetails: function(){
        this.set('isEdit', false);
      },      
      editrule: function(rule){
        console.log('handling editrule event');
        console.log('rule id is ' + rule.id);
        this.get('controllers.listRule').set('model', rule);
        this.get('controllers.setRule').set('model', rule);
        return false;
      },
      removerule: function(rule){
        console.log('handling removerule event');
        this.get('plan.refsetRules').removeObject(rule);
        return false;
      },    
      setterminal: function(ruleid){
        console.log('handling setterminal event');
        this.set('plan.terminal', ruleid);
        return false;
      },
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
        newRule.set('concepts', Ember.A());
        this.set('counter', this.get('counter') - 1);
        this.get('controllers.listRule').set('model', newRule);
        return false;
      }
    },  
    needs: ["index", "setRule", "listRule"]
  });

  MyApp.RulesController.reopenClass({
    getRefset: function(pageIndex, pageSize){
      return Ember.Deferred.promise(function(p) {
        var startIndex = (pageIndex - 1) * pageSize;
        var currentUrl = "http://" + location.hostname + ":" + location.port + "/" + location.pathname;
        var readyUrl = currentUrl.substring(0, currentUrl.lastIndexOf('/'));
        var query = readyUrl + ".json?start=" + startIndex + "&rows=" + pageSize;
        console.log('executing refset query: ' + query);
        p.resolve($.getJSON(query)
          .then(function(refset) {
              console.log('initialised rules controller with model ' + JSON.stringify(refset));
              var emberObject = MyApp.toEmberObject(refset);
              console.log('After Ember treatment, model is ' + JSON.stringify(emberObject));
              return emberObject;
          }) //then
        );//resolve
      });//deferred promise
    },//getConcepts
  });//reopen

  MyApp.RulesRoute = Ember.Route.extend({
    model: function(){
      console.log('In RulesRoute, loading Refset');
      var myrefset = MyApp.RulesController.getRefset(-1, -1);
      
      return myrefset;
    },
    actions: {
      update: function(){
        console.log('handling update action, about to call ajax');
        console.log('controller is ' + this.get("controller"));
        console.log('controller model is ' + this.get('controller.model'));
        console.log('body is ' + JSON.stringify(this.get('controller.model')));
        console.log('number of rules submitted: ' + this.get('controller.model.plan.refsetRules'));
        var _this = this;
        return Ember.Deferred.promise(function(p) {
          p.resolve(
            $.ajax({
              headers: {          
                 Accept : "application/json; charset=utf-8",         
                "Content-Type": "application/json; charset=utf-8"   
              },
              url: "put",
              type: "POST",
              data: JSON.stringify(_this.get('controller.model')),
              dataType: "json"
            })
            .then(function(response) {
              console.log('Response to update was [' + JSON.stringify(response) + ']');
              var emberResponse =  MyApp.toEmberObject(response);
              if (response.success){
                _this.set('controller.model', emberResponse.refset);
              }
              _this.controllerFor('updateResponse').set('model', emberResponse);
            }) //then
          );//resolve
        });//deferred promise
      }
    }
  })

  MyApp.RulesView = Ember.View.extend({
    templateName: 'rules'
  });

// LIST RULE MODAL  
  MyApp.ListRuleController = Ember.ObjectController.extend({
    model: undefined,
    needs: "rules",
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
        console.log('handling save');
        var rules = this.get('controllers.rules.model.plan.refsetRules');
        var found = rules.findBy('id', this.get('model.id'));
        console.log('found is ' + JSON.stringify(found));
        if (found === undefined){
          console.log('found is null, pushing object');
          rules.pushObject(this.get('model'));
        }else{
          console.log('found is not null, replacing');
          console.log('index of is ' + rules.indexOf(found));
          rules.removeAt(rules.indexOf(found));
          rules.pushObject(this.get('model'));
        }
        return false;
      },
      removeconcept: function(concept){
        console.log('removing concept ' + JSON.stringify(concept));
        //var concepts = this.get('model');
        //var emberArray = Ember.A();
        //for (i = 0; i < concepts.length; i++){
        //  if (concepts.objectAt(i).id != concept.id){
        //    emberArray.pushObject(concepts.objectAt(i));
        //  }
        //}
        //this.set('model', emberArray);
        console.log('model is ' + JSON.stringify(this.get('model')));
        this.get('concepts').removeObject(concept);
      },      
      click: function(concept){
        console.log('handling add concept click event');
        var found = this.get('concepts').findBy('id', concept.get('id'));
        if (found === undefined){
          this.get('concepts').pushObject(concept);
          this.set('isSearching', false);
        }else{
          console.log('can not add concept that already exists in selection');
        }
        return false;
      }
     }    
  });

  MyApp.ListRuleRoute = Ember.ObjectController.extend({
    renderTemplate: function(){
      
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
    model: undefined,
    appName: 'Set Rule',
    needs: "rules",

    actions:{
      saverule: function(){
        this.get('controllers.rules.model.plan.refsetRules').pushObject(this.get('model'));
        return false;
      },
      setLeft: function(ruleId){
        console.log('handling set left');
        this.set('left', ruleId);
        return false;
      },
      setRight: function(ruleId){
        console.log('handling set right');
        this.set('right', ruleId);
        return false;
      }
    }    
  });

  MyApp.SetRuleView = Ember.View.extend({
      templateName: 'setRule'
  });


// DISPLAY RULE
  MyApp.RuleController = Ember.ObjectController.extend({
    model: undefined,
    isList: function(){
      return (this.get('type') === "LIST");
    }.property('type'),
    isUnion: function(){
      return (this.get('type') === "UNION");
    }.property('type'),
    isDifference: function(){
      return (this.get('type') === "DIFFERENCE");
    }.property('type'),
    isIntersection: function(){
      return (this.get('type') === "INTERSECTION");
    }.property('type'),
    isSymDifference: function(){
      return (this.get('type') === "SYMMETRIC");
    }.property('type'),
    needs: "rules"
  });
  
  MyApp.RuleView = Ember.View.extend({
    templateName: 'rule',

  });  

  MyApp.UpdateResponseRoute = Ember.Route.extend({
    
  });

  MyApp.UpdateResponseController = Ember.ObjectController.extend({
    model: undefined,
    mysuccess: function(){
      console.log('in success. Model is [' + JSON.stringify(this.get('model.success')) + ']');

    }.property('model')

  });

