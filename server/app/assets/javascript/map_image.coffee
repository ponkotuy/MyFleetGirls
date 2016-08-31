
DefaultSize = 48
DefaultFont = "#{DefaultSize}px 'sans-serif'"
DefaultColor = '#006400'

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
    pos = @getPos(cell)
    @image.setPoint(pos.x, pos.y)

  setLine: (start, end) ->
    @image.clear()
    start = @getPos(start)
    end = @getPos(end)
    @image.setLine(start.x, start.y, end.x, end.y)

  getPos: (cell) ->
    p = @positions[cell]
    p ?= @positions[@cellInfos[cell]]
    p

  runClick: (x, y) =>
    cell = _.findIndex @positions, (pos) ->
      (pos.x - DefaultSize / 2) < x and
        x < (pos.x + DefaultSize / 2) and
        (pos.y - DefaultSize / 2) < y and
        y < (pos.y + DefaultSize / 2)
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
    @ctx.fillStyle = DefaultColor
    @ctx.fillText('★', x - DefaultSize / 2, y + DefaultSize / 3.5)

  setLine: (x1, y1, x2, y2) ->
    @ctx.fillStyle = DefaultColor
    @ctx.strokeStyle = DefaultColor
    arrow(@ctx, x1, y1, x2, y2, 20, 40, 30, 7)

  clear: () -> @setImage()

  onload: () -> @setImage()

  onclick: (x, y) ->

arrow = (ctx, ax, ay, bx, by_, w, h, h2, width) ->
  vx = bx - ax
  vy = by_ - ay
  v = Math.sqrt(vx * vx + vy * vy)
  ux = vx / v
  uy = vy / v
  lx = bx - uy * w - ux * h
  ly = by_ + ux * w - uy * h
  rx = bx + uy * w - ux * h
  ry = by_ - ux * w - uy * h
  mx = bx - ux * h2
  my = by_ - uy * h2
  line(ctx, ax, ay, bx - ux * width, by_ - uy * width, width)
  triangle(ctx, bx, by_, lx, ly, mx, my)
  triangle(ctx, bx, by_, rx, ry, mx, my)

line = (ctx, x1, y1, x2, y2, width) ->
  ctx.beginPath()
  ctx.lineWidth = width
  ctx.moveTo(x1, y1)
  ctx.lineTo(x2, y2)
  ctx.stroke()

triangle = (ctx, x1, y1, x2, y2, x3, y3) ->
  path = new Path2D()
  path.moveTo(x1, y1)
  path.lineTo(x2, y2)
  path.lineTo(x3, y3)
  ctx.fill(path)
