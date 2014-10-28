$(document).ready ->
  $('.panel').each ->
    elem = $(this)
    id = elem.attr('id')
    slot = elem.attr('data-slot')
    vue = new Vue
      el: '#' + id
      data:
        logs: []
        url: ""
      methods:
        getJSON: ->
          @setHash()
          $.getJSON @url, (data) => @logs = data
        setHash: ->
          location.hash = toURLParameter({id: slot})
        restoreHash: ->
          obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        shipName: (log) -> if log.ship? then log.ship.name else "無し"
        dayOfWeek: (date) -> moment(date).locale('ja').format('dddd')
        dateFormat: (date) -> moment(date).format('YYYY-MM-DD HH:mm')
      created: ->
        i = this
        elem.find('.panel-collapse').on 'show.bs.collapse', ->
          i.restoreHash()
          i.url = $(this).attr('data-url')
          if i.logs.length == 0
            i.getJSON()
      ready: ->
        @$watch 'logs', (logs) ->
          if logs.length > 0
            $("#panel#{slot}")[0].scrollIntoView(true)

  obj = fromURLParameter(location.hash.replace(/^\#/, ''))
  $("#collapse#{obj.id}").collapse()
