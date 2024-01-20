package demo

import indigo.*

final case class Enemy(id: EnemyId, position: Point, state: EnemyState):
  def kill: Enemy = this.copy(state = EnemyState.Dead)
  def isDead: Boolean = state match
    case EnemyState.Alive => false
    case EnemyState.Dead  => true

  def bump(pt: Point, player: Point): Outcome[Enemy] =
    pt match
      case p if p == player => Outcome(this).addGlobalEvents(GameEvent.KillPlayer(id, player))
      case p                => Outcome(this.copy(position = p))

object Enemy:
  def spawn(dice: Dice, position: Point): Enemy = Enemy(EnemyId(dice), position, EnemyState.Alive)

enum EnemyState:
  case Alive, Dead

opaque type EnemyId = String
object EnemyId {

  def apply(dice: Dice): EnemyId = dice.rollAlphaNumeric(10)

}
