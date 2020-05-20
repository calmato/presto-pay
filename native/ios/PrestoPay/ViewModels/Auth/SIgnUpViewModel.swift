import Foundation
import Combine

final class SignUpViewModel: ObservableObject {
  // MARK: Private
  private let authProvider: AuthProviderProtocol
  private var cancellable: AnyCancellable?
  
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
  init (authProvider: AuthProviderProtocol = AuthProvider()) {
    self.authProvider = authProvider
  }
  
  // MARK: Action
  func signUp() {
    validationError = ""
    
    cancellable = authProvider.signUp(
      name: name,
      username: username,
      email: email,
      thumbnail: thumbnail,
      password: password,
      passwordConfirmation: passwordConfirmation
    )
      .receive(on: RunLoop.main)
      .sink(receiveCompletion: { completion in
        switch completion {
        case .failure:
          self.validationError = "Error" // TODO: Edit
        case .finished:
          self.validationError = "Success" // TODO: Edit
        }
      }, receiveValue: { value in
        print("value: \(value)")
      })
  }
}
