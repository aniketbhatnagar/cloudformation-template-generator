package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{JsString, JsValue, JsonFormat, DefaultJsonProtocol}

class Runtime(val runtime: String)

case object NodeJS extends Runtime("nodejs")

case object Java8 extends Runtime("java8")

case object Python27 extends Runtime("python2.7")

object Runtime {

  implicit object format extends JsonFormat[Runtime] {
    override def write(obj: Runtime) = JsString(obj.runtime)

    override def read(json: JsValue): Runtime = json match {
      case JsString(runtime) => new Runtime(runtime)
    }
  }

}

case class `AWS::Lambda::Function`(name: String,
                                   Code: Code,
                                   Description: Option[String],
                                   Handler: String,
                                   Runtime: Runtime,
                                   MemorySize: Option[Int] = None,
                                   Role: Token[String],
                                   Timeout: Option[Int] = None,
                                   override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Lambda::Function`] with HasArn with Subscribable {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def asSubscription = Subscription(
    Endpoint = arn,
    Protocol = "lambda"
  )

}

object `AWS::Lambda::Function` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Function`] = jsonFormat9(`AWS::Lambda::Function`.apply)
}

case class Code(S3Bucket: Option[Token[String]],
                S3Key: Option[Token[String]],
                S3ObjectVersion: Option[Token[String]],
                ZipFile: Option[String])

object Code extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Code] = jsonFormat4(Code.apply)
}

case class `AWS::Lambda::Permission`(name: String,
                                     Action: String,
                                     FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                     Principal: Token[String],
                                     SourceAccount: Option[Token[String]],
                                     SourceArn: Option[Token[String]],
                                     override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Lambda::Permission`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::Lambda::Permission` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Permission`] = jsonFormat7(`AWS::Lambda::Permission`.apply)
}

case class `AWS::Lambda::EventSourceMapping`(
                                              name: String,
                                              BatchSize: Option[Token[Int]],
                                              Enabled: Option[Token[Boolean]],
                                              EventSourceArn: Token[String],
                                              FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                              StartingPosition: Token[String],
                                              override val Condition: Option[ConditionRef] = None
                                              ) extends Resource[`AWS::Lambda::EventSourceMapping`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::Lambda::EventSourceMapping` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::EventSourceMapping`] = jsonFormat7(`AWS::Lambda::EventSourceMapping`.apply)
}

