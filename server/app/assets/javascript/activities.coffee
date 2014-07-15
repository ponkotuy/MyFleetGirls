$(document).ready () ->
  vue = new Vue
    el: '#activities_list'
    data:
      acts: []
      from: 0
    methods:
      getJSON: () ->
        $.getJSON('/rest/v1/activities', {from: @from})
          .done (data) =>
            @acts = data.concat(@acts)
            @from = (new Date).getTime()
      jump: (href) ->
        location.href = href
      tformat: (t) -> moment(t).format('YYYY-MM-DD HH:mm:ss')
    ready: ->
      gJ = () => @getJSON()
      gJ()
      setInterval(gJ, 10*1000)
