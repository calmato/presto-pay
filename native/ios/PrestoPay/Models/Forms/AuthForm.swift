import Foundation

struct AuthLoginForm: Codable {
  let email: String
  let password: String
}

struct AuthSignUpForm: Codable {
  let name: String
  let username: String
  let email: String
  let thumbnail: String
  let password: String
  let passwordConfirmation: String
}
