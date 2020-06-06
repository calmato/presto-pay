import Combine

// Mark: Session - 認証情報用のモデル
class AuthState: ObservableObject {
  @Published var token: String

  static let shared = AuthState()

  init(token: String = "") {
    self.token = token
  }
}
