
DefaultSize = 48
DefaultFont = "#{DefaultSize}px 'sans-serif'"

class @SeaMap
  constructor: (idTag) ->
    @image = new MapImage(idTag)
    @positions = []
    url = $('#' + idTag).attr('data-position')
    $.getJSON url, (data) =>
      data.forEach (d) =>
        @positions[d.cell] = {x: d.posX, y: d.posY}
      @image.onload = =>
        @onload()

  setPoint: (cell) ->
    @image.clear()
    p = @positions[cell]
    @image.setPoint(p.x, p.y)

  onload: () ->

class MapImage
  constructor: (idTag) ->
    @tag = $('#' + idTag)
    @ctx = @tag[0].getContext('2d')
    @loadImage()
    @setImage()

  loadImage: () ->
    @img = new Image()
    @img.src = @tag.attr('data-src')
    @img.onload = =>
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
