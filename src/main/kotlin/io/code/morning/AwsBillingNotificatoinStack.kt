package io.code.morning

import software.amazon.awscdk.core.App
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.core.StackProps
import software.amazon.awscdk.services.events.CronOptions
import software.amazon.awscdk.services.events.Rule
import software.amazon.awscdk.services.events.RuleProps
import software.amazon.awscdk.services.events.Schedule
import software.amazon.awscdk.services.events.targets.LambdaFunction
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.FunctionProps
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.s3.Bucket

class AwsBillingNotificatoinStack @JvmOverloads constructor(app: App, id: String, props: StackProps? = null) :
    Stack(app, id, props) {
  init {
    val bucketName = "morning-code-aws-billing-notification"
    val jarName = "aws-billing-notification-0.0.1-SNAPSHOT.jar"
    val lambda = Function(this, "lambda", FunctionProps.builder()
        .functionName("aws-billing-notificaion")
        .runtime(Runtime.JAVA_11)
        .handler("io.code.morning.AwsBillingNotification::handleRequest")
        .code(Code.fromBucket(Bucket.fromBucketName(this, "artifact-bucket", bucketName), jarName))
        .timeout(Duration.seconds(30))
        .build()
    )

    val slackWebhookUrl = ""
    lambda.addEnvironment("SLACK_WEBHOOK_URL", slackWebhookUrl)

    Rule(this, "rule", RuleProps.builder()
        .schedule(Schedule.cron(CronOptions.builder()
            .minute("0")
            .hour("8")
            .day("*")
            .month("*")
            .year("*")
            .build()
        ))
        .build()
    ).addTarget(LambdaFunction(lambda))
  }
}
