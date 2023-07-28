provider "aws" {
  region = local.region

  assume_role {
    role_arn = "arn:aws:iam::514563129364:role/ops-atlantis"
  }
}
