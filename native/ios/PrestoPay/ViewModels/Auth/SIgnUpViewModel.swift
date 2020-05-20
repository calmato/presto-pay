import Foundation
import Combine

final class SignUpViewModel: ObservableObject {
  // MARK: Private
  
  // MARK: Input
  @Published var name: String = ""
  @Published var username: String = ""
  @Published var email: String = ""
  @Published var thumbnail: String = ""
  @Published var password: String = ""
  @Published var passwordConfirmation: String = ""
  
  // MARK: Output
  @Published private(set) var validationError: String = ""
  
  // MARK: Initializer
  
  // MARK: Action
  func signUp() {}
}
