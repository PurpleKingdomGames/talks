package demo

import indigo.*

final case class Player(position: Point, state: PlayerState):
  def moveTo(pt: Point): Player =
    this.copy(position = pt)

  def kill(timeOfDeath: Seconds): Player =
    this.copy(state = PlayerState.Dead(timeOfDeath))

enum PlayerState:
  case Alive
  case Dead(timeOfDeath: Seconds)

  def isAlive: Boolean =
    this match
      case Alive => true
      case _     => false

  def isDead: Boolean =
    this match
      case Dead(_) => true
      case _       => false

  def proclaimTimeOfDeath: Option[Seconds] =
    this match
      case Alive             => None
      case Dead(timeOfDeath) => Option(timeOfDeath)
