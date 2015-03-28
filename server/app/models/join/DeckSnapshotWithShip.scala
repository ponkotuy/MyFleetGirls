package models.join

import models.db.{Admiral, DeckSnapshot}

/**
 * Date: 14/06/16.
 */
case class DeckSnapshotWithShip(deck: DeckSnapshot, ships: Seq[ShipSnapshotWithName]) extends DeckSnapshotWith

case class DeckSnapshotWithAdmiral(deck: DeckSnapshot, ships: Seq[ShipSnapshotWithName], admiral: Admiral) extends DeckSnapshotWith

trait DeckSnapshotWith {
  def deck: DeckSnapshot
  def ships: Seq[ShipSnapshotWithName]

  def id = deck.id
  def title = deck.title
  def comment = deck.comment
  def comments = deck.comment.split('\n')
  def memberId = deck.memberId
  def created = deck.created
}
