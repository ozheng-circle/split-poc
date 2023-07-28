terraform {
  backend "s3" {
    bucket         = "circle-tf-514563129364-us-east-1"
    key            = "tf/ops/sandbox/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = "true"
    dynamodb_table = "circle-tf-lock-514563129364-us-east-1"
    role_arn       = "arn:aws:iam::514563129364:role/ops-atlantis"
  }

    required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4"
    }
    argocd = {
      source  = "oboukili/argocd"
      version = "~> 4"
    }
  }
}
