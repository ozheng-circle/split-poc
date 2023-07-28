module "argocd_application" {
  # Please refer to https://github.com/circlefin/terraform-aws-argocd-application
  # for usage instructions
  source = "git@github.com:circlefin/terraform-aws-argocd-application.git?ref=1.5.0"

  app_name       = local.app
  argocd_project = "online-infrastructure"
  git_branch     = "master"

  # Enabling this will prevent you from applying this locally. Change to false to apply locally.
  # Only changes how embedded argocd provider behaves.
  argocd_atlantis_apply = true

  deploy_environments = {
    smokebox = {
      account     = "pay"
      environment = "smokebox"
      cluster     = "app"
    }
  }
}
