package dat

import models.DeckSnapshot

/**
 * Date: 14/06/16.
 */
case class DeckSnapshotWithShip(deck: DeckSnapshot, ships: Seq[ShipSnapshotWithName]) {
  def id = deck.id
  def title = deck.title
  def comment = deck.comment
  def memberId = deck.memberId
}
