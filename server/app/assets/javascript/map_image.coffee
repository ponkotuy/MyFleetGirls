
DefaultSize = 48
DefaultFont = "#{DefaultSize}px 'sans-serif'"

class @SeaMap
  constructor: (idTag) ->
    @image = new MapImage(idTag)
    @image.onclick = @runClick
    @positions = []
    @cellInfos = {}
    @toAlpha = []
    @load(idTag)

  load: (idTag) ->
    posUrl = $('#' + idTag).attr('data-position')
    infoUrl = $('#' + idTag).attr('data-cellinfo')
    @image.onload = =>
      $.getJSON posUrl, (data) =>
        data.forEach (d) =>
          @positions[d.cell] = {x: d.posX, y: d.posY}
        if infoUrl
          $.getJSON infoUrl, (data) =>
            data.forEach (d) =>
              @cellInfos[d.alphabet] = d.cell
              @toAlpha[d.cell] = d.alphabet
            @onload()
        else
          @onload()

  setPoint: (cell, fixed) ->
    if fixed
      @fix = cell
    @image.clear()
    p = @positions[cell]
    p ?= @positions[@cellInfos[cell]]
    @image.setPoint(p.x, p.y)

  runClick: (x, y) =>
    cell = _.findIndex @positions, (pos) ->
      (pos.x - DefaultSize / 2) < x and x < (pos.x + DefaultSize / 2) and (pos.y - DefaultSize / 2) < y and y < (pos.y + DefaultSize / 2)
    alpha = @toAlpha[cell]
    @onclick(alpha)

  onload: ->

  onclick: (alpha) ->

  clear: ->
    @image.clear()
    if @fix
      @setPoint(@fix, false)


class MapImage
  constructor: (idTag) ->
    @tag = $('#' + idTag)
    @ctx = @tag[0].getContext('2d')
    @loadImage()
    @setImage()
    that = @
    @tag.click (event) ->
      dElm = document.documentElement
      dBody = document.body
      nX = dElm.scrollLeft || dBody.scrollLeft
      nY = dElm.scrollTop || dBody.scrollTop
      that.onclick(event.clientX - @offsetLeft + nX, event.clientY - @offsetTop + nY)

  loadImage: () ->
    @img = new Image()
    @img.src = @tag.attr('data-src')
    @img.onload = =>
      @setImage()
      @onload()

  setImage: () ->
    @ctx.globalAlpha = 1.0
    @ctx.drawImage(@img, 0, 0, @img.width, @img.height)

  setPoint: (x, y) ->
    @ctx.font = DefaultFont
    @ctx.globalAlpha = 0.8
    @ctx.fillStyle = '#006400'
    @ctx.fillText('★', x - DefaultSize / 2, y + DefaultSize / 3.5)

  clear: () -> @setImage()

  onload: () -> @setImage()

  onclick: (x, y) ->
